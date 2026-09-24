package cnpj.analyzr.payload.aws;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record AwsSnsMessageRequest(String notificationType, AwsSnsMessageMailRequest mail) {

    public static record AwsSnsMessageMailRequest(List<String> destination,
            LocalDateTime timestamp,
            String source,
            String sourceArn,
            String sourceIp,
            String callerIdentity,
            String sendingAccountId,
            String messageid,
            Boolean headersTruncated,
            Map<String, String> headers,
            AwsSnsMessageCommonHeadersRequest commonHeaders,
            AwsSnsMessageDeliveryRequest delivery) {
    }

    public static record AwsSnsMessageCommonHeadersRequest(List<String> from, String date, List<String> to,
            String messageId, String subject) {
    }

    public static record AwsSnsMessageDeliveryRequest(String timestamp, Integer processingTimeMillis,
            List<String> recipients, String smtpResponse,
            String remoteMtaIp, String reportingMTA) {
    }
}
