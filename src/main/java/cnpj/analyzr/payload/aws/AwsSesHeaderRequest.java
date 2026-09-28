package cnpj.analyzr.payload.aws;

import java.util.Optional;

public record AwsSesHeaderRequest(Long campaignRowId) {

    private static final String CAMPAIGN_ROW_ID_KEY = "campaignRowId=";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (campaignRowId != null) {
            sb.append(CAMPAIGN_ROW_ID_KEY)
                    .append(campaignRowId);
        }
        return sb.toString();
    }

    public static Optional<AwsSesHeaderRequest> fromHeader(String header) {
        if (header == null)
            return Optional.empty();

        try {
            String substring = header.substring(CAMPAIGN_ROW_ID_KEY.length());
            AwsSesHeaderRequest request = new AwsSesHeaderRequest(Long.parseLong(substring));
            return Optional.ofNullable(request);
        } catch (Exception e) {
        }
        return Optional.empty();
    }
}
