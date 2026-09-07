package cnpj.analyzr.campaign;

import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cnpj.analyzr.campaign.CampaignController.CampaignExecute;
import cnpj.analyzr.email.EmailService;
import cnpj.analyzr.email.EmailService.EmailResult;
import cnpj.analyzr.lead.LeadRecord;
import cnpj.analyzr.lead.LeadRepository;
import cnpj.analyzr.template.Template;
import cnpj.analyzr.template.TemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignService {

    private final EmailService emailSenderService;

    private final LeadRepository leadRepository;
    private final TemplateRepository templateRepository;
    private final CampaignRepository campaignRepository;

    @Async("threadPoolTaskExecutor")
    public void execute(CampaignExecute body) {
        log.info("execute - body:{}", body);
        if (body.filter() == null || body.limit() == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        try {
            long startTime = System.currentTimeMillis();
            Template template = templateRepository.find(body.templateId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(400)));

            List<LeadRecord> leads = leadRepository.find(body.filter().withLimit(body.limit()));

            Long campaignId = campaignRepository.insert(body.description(), template.id(), leads.size());

            log.info("execute - sending email for {} customers, template_id: {}", leads.size(), template.id());

            // mailgun did not handle parallel stream well
            leads.stream().forEach(lead -> {
                EmailResult result = emailSenderService.send(lead.email(), template.subject(), template.body());

                if (result.success()) {
                    int sendQuantity = lead.send() == null ? 1 : (lead.send() + 1);
                    leadRepository.update(lead.id(), sendQuantity);
                }
                campaignRepository.insertRow(campaignId, lead.id(), result.success(), result.message());

                log.info("execute campaign - response:{} company email:{}", result, lead.email());
            });

            long endTime = System.currentTimeMillis();
            log.info("execute - finished in {} seconds", ((endTime - startTime) / 1000));
        } catch (Exception e) {
            log.error("execute: ", e);
        }
    }
}
