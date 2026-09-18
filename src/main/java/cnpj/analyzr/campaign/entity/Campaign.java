package cnpj.analyzr.campaign.entity;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record Campaign(
        Long id,
        Long templateId,
        String description,
        LocalDateTime createdAt,
        Integer rowCount) {
}
