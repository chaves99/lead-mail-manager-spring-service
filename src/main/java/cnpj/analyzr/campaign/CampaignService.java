package cnpj.analyzr.campaign;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private static final String CAMPAIGN_ROW_ID_REPLACABLE_TOKEN = "ROW_ID";
    private static final String URL_TARGET_REPLACABLE_TOKEN = "URL_TARGET";

    private static final String TRACKABLE_URL_PARAMS = "?campaign_row_id=" + CAMPAIGN_ROW_ID_REPLACABLE_TOKEN
            + "&url_target=" + URL_TARGET_REPLACABLE_TOKEN;

    private static final String CLICK_TRACKABLE_URL = "https://lead-mail-manager-spring-service-production.up.railway.app/statistics/track-click"
            + TRACKABLE_URL_PARAMS;

    private static final String IMG_OPEN_EMAIL_TRACK = "<img src=\"https://lead-mail-manager-spring-service-production.up.railway.app/statistics/track-open?campaign_row_id="
            + CAMPAIGN_ROW_ID_REPLACABLE_TOKEN + "\" width=\"1\" height=\"1\" alt=\"\" />";

    private final EmailService emailSenderService;

    private final LeadRepository leadRepository;
    private final TemplateRepository templateRepository;
    private final CampaignRepository campaignRepository;
    private final CampaignRowRepository campaignRowRepository;

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

            Long campaignId = campaignRepository.insert(body.description(),
                    template.id(), leads.size());

            log.info("execute - sending email for {} customers, template_id: {}", leads.size(), template.id());

            int countErrors = 0;
            int countSuccess = 0;

            for (var lead : leads) {
                long rowId = campaignRowRepository.insertRow(campaignId, lead.id());

                String emailBody = prepareEmailBody(rowId, template.body());

                EmailResult result = emailSenderService.send(lead.email(),
                        template.subject(), emailBody);

                campaignRowRepository.updateEmailResponse(rowId, result);
                if (result.success()) {
                    int sendQuantity = lead.send() == null ? 1 : (lead.send() + 1);
                    leadRepository.updatePlusCounter(lead.id(), sendQuantity);
                    countSuccess++;
                } else {
                    log.warn("error to send email:{} meassage:{}", lead.email(), result.message());
                    countErrors++;
                }

                // log.info("execute campaign - response:{} company email:{}", result,
                //         lead.email());
            }

            long endTime = ((System.currentTimeMillis() - startTime) / 1000);

            log.info("execute - finished in {} seconds success:{} error:{}",
                    endTime, countSuccess, countErrors);
        } catch (Exception e) {
            log.error("execute: ", e);
        }
    }

    @Transactional
    public void undo(Long id) {
        log.info("undo - id:{}", id);
        campaignRepository.find(id).ifPresent(campaign -> {
            List<CampaignRow> campaignRows = campaignRowRepository.findAll(id);
            List<LeadRecord> leads = leadRepository.find(campaignRows.stream().map(CampaignRow::leadId).toList());

            leads.forEach(lead -> {
                Integer send = lead.send();
                if (send > 0) {
                    leadRepository.updateCounter(lead.id(), send - 1);
                }
            });

            log.info("undo - campaignRows:{} leads:{}", campaignRows.size(), leads.size());

            campaignRowRepository.deleteAll(id);
            campaignRepository.update(id, campaign.description() + " - [UNDO]");
        });
    }

    private String prepareEmailBody(Long rowId, String emailBody) {
        Document document = Jsoup.parse(emailBody);
        Element htmlElement = document.select("body").first();
        htmlElement.append(IMG_OPEN_EMAIL_TRACK.replace(CAMPAIGN_ROW_ID_REPLACABLE_TOKEN, rowId.toString()));

        findLinks(document, rowId);
        return document.toString();
    }

    private void findLinks(Document document, Long rowId) {
        Elements select = document.select("a[href]");
        select.forEach(e -> {
            Attribute attribute = e.attribute("href");
            String value = attribute.getValue();
            String finalTrackableUrl = CLICK_TRACKABLE_URL
                    .replace(CAMPAIGN_ROW_ID_REPLACABLE_TOKEN, rowId.toString())
                    .replace(URL_TARGET_REPLACABLE_TOKEN, value);
            e.attribute("href").setValue(finalTrackableUrl);
        });
    }
}
