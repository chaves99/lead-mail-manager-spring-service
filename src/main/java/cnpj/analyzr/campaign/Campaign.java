package cnpj.analyzr.campaign;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record Campaign(
        Long id,
        Long templateId,
        String description,
        LocalDateTime createdAt,
        List<CampaignRow> campaigns) {

    public static record CampaignRow(Long id, Long companyId, boolean success) {
    }
}
