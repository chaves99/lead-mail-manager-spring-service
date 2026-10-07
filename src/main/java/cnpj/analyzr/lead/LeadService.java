package cnpj.analyzr.lead;

import java.util.List;

import org.jsoup.HttpStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cnpj.analyzr.campaign.row.CampaignRow;
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

    public void create(String email) {
        leadRepository.insert(List.of(email));
    }

    public void createBatch(List<String> emails) {
        leadRepository.insert(emails);
    }

    public LeadTotalDashboard getTotalDashboard() {
        return leadRepository
                .findTotalDashboard()
                .withRegistered(leadRepository.countNoFilter());

    }

    public void unsubscribe(Long leadId, Long rowId) throws HttpStatusException {
        CampaignRow campaignRow = campaignRowRepository
                .findByIdAndLeadId(rowId, leadId)
                .orElseThrow(() -> new HttpStatusException("", HttpStatus.NOT_FOUND.value(), null));
        log.info("unsubscribe - leadId:{} rowId:{}", leadId, rowId);
        leadRepository.unsubscribe(campaignRow.leadId());
    }

    @Transactional
    public void delete(Long id) {
        campaignRowRepository.deleteByLead(id);
        leadRepository.delete(id);
    }

}
