package cnpj.analyzr.campaign.entity.req;

import cnpj.analyzr.lead.LeadFilterRecord;

public record CampaignExecuteRecordRequest(String description,
        Long templateId,
        Integer limit,
        LeadFilterRecord filter) {
}
