package cnpj.analyzr.statistics;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import cnpj.analyzr.campaign.CampaignRowRepository;
import cnpj.analyzr.lead.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final LeadRepository leadRepository;

    private final CampaignRowRepository campaignRowRepository;

    public void opened(Long campaignRowId) {
        try {
            campaignRowRepository.updateOpen(campaignRowId);
            campaignRowRepository
                    .find(campaignRowId)
                    .flatMap(cr -> leadRepository.find(cr.leadId()))
                    .ifPresent(lead -> {
                        log.info("email opened: {}", lead.email());
                        leadRepository.updateOpen(lead.id(), lead.open() + 1);
                    });
        } catch (Exception e) {
            // log.error("opened - exception: ", e);
        }
    }

    @Async
    public void trackClick(Long campaignRowId) {
        try {
        campaignRowRepository.updateClick(campaignRowId);
        campaignRowRepository
                .find(campaignRowId)
                .flatMap(cr -> leadRepository.find(cr.leadId()))
                .ifPresent(lead -> {
                    log.info("trackClick - lead: {}", lead.email());
                    leadRepository.updateClick(lead.id(), lead.click() + 1);
                });
        } catch(Exception e) {
            log.info("trackClick - excpetion: {}", e.getMessage());
        }
    }
}
