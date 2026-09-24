package cnpj.analyzr.campaign;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cnpj.analyzr.campaign.entity.req.CampaignExecuteRecordRequest;
import cnpj.analyzr.campaign.entity.res.CampaignDetailsRecordResponse;
import cnpj.analyzr.campaign.row.CampaignRow;
import cnpj.analyzr.campaign.row.CampaignRowRepository;
import cnpj.analyzr.campaign.row.CampaignRowRepository.CampaignRowTotalsRecord;
import cnpj.analyzr.email.EmailServiceInterface;
import cnpj.analyzr.email.EmailServiceInterface.EmailResult;
import cnpj.analyzr.lead.LeadRecord;
import cnpj.analyzr.lead.LeadRepository;
import cnpj.analyzr.template.Template;
import cnpj.analyzr.template.TemplateRepository;
import cnpj.analyzr.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignService {

    @Qualifier("awsSesEmailServiceImpl")
    private final EmailServiceInterface awsSesEmailServiceImpl;

    private final LeadRepository leadRepository;
    private final TemplateRepository templateRepository;
    private final CampaignRepository campaignRepository;
    private final CampaignRowRepository campaignRowRepository;

    @Async("threadPoolTaskExecutor")
    public void execute(CampaignExecuteRecordRequest body) {
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

                String emailBody = EmailUtils.prepareEmailBody(rowId, template.body());

                EmailResult result = awsSesEmailServiceImpl.send(lead.email(),
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

    public CampaignDetailsRecordResponse getDetails(Long campaignId) {
        return campaignRepository
                .find(campaignId)
                .map(campaign -> {
                    CampaignRowTotalsRecord totalsByCampaignid = campaignRowRepository
                            .findTotalsByCampaignid(campaignId);
                    String templateName = null;
                    Optional<Template> template = templateRepository.find(campaign.templateId());
                    if (template.isPresent())
                        templateName = template.get().name();

                    campaign.templateId();
                    return new CampaignDetailsRecordResponse(campaign, templateName, totalsByCampaignid);
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
