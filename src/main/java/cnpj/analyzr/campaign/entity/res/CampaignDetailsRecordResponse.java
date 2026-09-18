package cnpj.analyzr.campaign.entity.res;

import cnpj.analyzr.campaign.entity.Campaign;
import cnpj.analyzr.campaign.row.CampaignRowRepository.CampaignRowTotalsRecord;
import lombok.Builder;

@Builder
public record CampaignDetailsRecordResponse(Campaign campaign, String templateName, CampaignRowTotalsRecord rowsTotal) {
}
