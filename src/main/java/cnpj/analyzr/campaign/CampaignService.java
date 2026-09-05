package cnpj.analyzr.campaign;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cnpj.analyzr.campaign.CampaignController.CampaignExecute;
import cnpj.analyzr.company.Company;
import cnpj.analyzr.company.CompanyController.EmailCompanyResponse;
import cnpj.analyzr.company.CompanyRepository;
import cnpj.analyzr.email.EmailService;
import cnpj.analyzr.email.EmailService.EmailResult;
import cnpj.analyzr.payload.GetCompanyRequestFilter;
import cnpj.analyzr.payload.GetEmailRequestFilter;
import cnpj.analyzr.template.Template;
import cnpj.analyzr.template.TemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignService {

    private final EmailService emailSenderService;

    private final TemplateRepository templateRepository;
    private final CompanyRepository companyRepository;
    private final CampaignRepository campaignRepository;

    @Async("threadPoolTaskExecutor")
    public void execute(CampaignExecute body) {
        if (body.emailFilter() == null && body.companyFilter() == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        try {
            long startTime = System.currentTimeMillis();
            Template template = templateRepository.find(body.templateId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(400)));

            Pattern pattern = Pattern.compile("[[A-Za-z_]*]");
            final boolean containTagOnSubject = pattern.matcher(template.subject()).find();
            final boolean containTagOnBody = pattern.matcher(template.body()).find();

            List<Company> list;
            boolean applyFilter = body.limit() != null && body.limit() > 0;
            if (body.companyFilter() != null) {
                GetCompanyRequestFilter filter = body.companyFilter();

                if (applyFilter) {
                    filter = body.companyFilter().withLimit(body.limit());
                }
                list = companyRepository.find(filter);
            } else {
                GetEmailRequestFilter filter = body.emailFilter();
                if (applyFilter) {
                    filter = body.emailFilter().withLimit(body.limit());
                }
                Collection<String> emails = companyRepository.groupByEmail(filter)
                        .stream()
                        .map(EmailCompanyResponse::email)
                        .collect(Collectors.toCollection(HashSet::new));
                list = companyRepository.findByEmails(emails);
            }

            Long campaignId = campaignRepository.insert(body.description(), template.id(), list.size());

            log.info("execute - sending email for {} customers, template_id: {}", list.size(), template.id());

            // mailgun did not handle parallel stream well
            list.stream().forEach(company -> {
                String bodyParsed = containTagOnBody ? parse(template.body(), company) : template.body();
                String subject = containTagOnSubject ? parse(template.subject(), company) : template.subject();
                EmailResult result = emailSenderService.send(company.email(), subject, bodyParsed);

                campaignRepository.insertRow(campaignId, company.id(), result.success(), result.message());

                log.info("execute campaign - response:{} company email:{}", result, company.email());
            });

            long endTime = System.currentTimeMillis();
            log.info("execute - finished in {} seconds", ((endTime - startTime) / 1000));
        } catch (Exception e) {
            log.error("execute: " + e);
        }
    }

    public String parse(String text, Company company) {
        if (text.contains("[fantasy_name]")) {
            text = text.replaceAll("[fantasy_name]", company.fantasyName());
        }
        return text;
    }
}
