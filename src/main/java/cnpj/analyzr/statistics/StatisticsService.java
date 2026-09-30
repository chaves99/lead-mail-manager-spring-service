package cnpj.analyzr.statistics;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import cnpj.analyzr.campaign.row.CampaignRowRepository;
import cnpj.analyzr.lead.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final LeadRepository leadRepository;

    private final CampaignRowRepository campaignRowRepository;

    public void trackOpen(Long campaignRowId) {
        try {
            campaignRowRepository
                    .findById(campaignRowId)
                    .ifPresent(campaignRow -> {
                        if (campaignRow.sendDate().isBefore(LocalDateTime.now().minusMinutes(2))) {
                            campaignRowRepository.updateOpen(campaignRowId);
                            leadRepository.find(campaignRow.leadId())
                                    .ifPresent(lead -> {
                                        log.info("trackOpen - email opened: {}", lead.email());
                                        leadRepository.updateOpen(lead.id(), lead.open() + 1);
                                    });
                        } else {
                            log.info("trackOpen - NOT IN RANGE TIME - campaign row id: {}", campaignRowId);
                        }
                    });
        } catch (Exception e) {
            log.error("opened - exception: ", e);
        }
    }

    @Async
    public void trackClick(Long campaignRowId) {
        try {
            campaignRowRepository
                    .findById(campaignRowId)
                    .ifPresent(campaignRow -> {
                        if (campaignRow.sendDate().isBefore(LocalDateTime.now().minusMinutes(2))) {
                            campaignRowRepository.updateClick(campaignRowId);
                            leadRepository.find(campaignRow.leadId()).ifPresent(lead -> {
                                log.info("email clicked: {}", lead.email());
                                leadRepository.updateClick(lead.id(), lead.click() + 1);
                            });
                        } else {
                            log.info("trackClick - NOT IN RANGE TIME - campaign row id: {}", campaignRowId);
                        }
                    });
        } catch (Exception e) {
            log.info("trackClick - excpetion: {}", e.getMessage());
        }
    }
}
