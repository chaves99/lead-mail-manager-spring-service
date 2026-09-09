package cnpj.analyzr.campaign;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record CampaignRow(
        Long id,
        Long leadId,
        boolean success,
        String errorMsg,
        LocalDate sendDate,
        boolean opened,
        boolean clicked,
        LocalDate clickedDate,
        Long campaignId) {
}
