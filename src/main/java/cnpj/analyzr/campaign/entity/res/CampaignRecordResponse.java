package cnpj.analyzr.campaign.entity.res;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record CampaignRecordResponse(Long id, String description,
        Long templateId, String templateName, Integer rowCount,
        LocalDateTime createdAt, Integer success, Integer failure) {
}
