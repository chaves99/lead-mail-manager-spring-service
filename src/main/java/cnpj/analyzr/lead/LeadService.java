package cnpj.analyzr.lead;

import java.util.List;

import org.springframework.stereotype.Service;

import cnpj.analyzr.campaign.row.CampaignRowRepository;
import cnpj.analyzr.lead.LeadController.LeadResponse;
import cnpj.analyzr.lead.LeadController.LeadTotalDashboard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    private final CampaignRowRepository campaignRowRepository;

    public LeadResponse fetch(LeadFilterRecord filter) {
        List<LeadRecord> body = leadRepository.find(filter);
        int count = leadRepository.count(filter);
        return new LeadResponse(count, body);
    }

    public LeadTotalDashboard getTotalDashboard() {
        return leadRepository
                .findTotalDashboard()
                .withRegistered(leadRepository.countNoFilter());

    }

    public void unsubscribe(Long campaignRowId) {
        campaignRowRepository
                .findById(campaignRowId)
                .ifPresentOrElse(
                        cr -> leadRepository.unsubscribe(cr.leadId()),
                        () -> log.info("unsubscribe - campaign row id not found:{}", campaignRowId));
    }

}
