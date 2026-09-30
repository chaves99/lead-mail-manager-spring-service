package cnpj.analyzr.campaign.row;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record CampaignRow(
        Long id,
        Long leadId,
        boolean success,
        String errorMsg,
        LocalDateTime sendDate,
        boolean opened,
        boolean clicked,
        LocalDate clickedDate,
        Long campaignId,
        String externalId) {

    @Getter
    @AllArgsConstructor
    public static enum Status {
        PENDING(1),
        DELIVERED(2),
        BOUNCED(3),
        COMPLAINED(4),
        UNKNOW_ERROR(5);

        private int code;

        public Optional<Status> from(int code) {
            for (var s : values()) {
                if (s.getCode() == code) {
                    return Optional.of(s);
                }
            }
            return Optional.empty();
        }
    }
}
