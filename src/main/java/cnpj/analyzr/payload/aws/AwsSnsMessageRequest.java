package cnpj.analyzr.payload.aws;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public record AwsSnsMessageRequest(AwsSnsNotificationTypeRequest notificationType, AwsSnsMessageMailRequest mail) {

    public static record AwsSnsMessageMailRequest(List<String> destination,
            LocalDateTime timestamp,
            String source,
            String sourceArn,
            String sourceIp,
            String callerIdentity,
            String sendingAccountId,
            String messageid,
            Boolean headersTruncated,
            List<AwsSnsMessageHeaderRequest> headers,
            AwsSnsMessageCommonHeadersRequest commonHeaders,
            AwsSnsMessageDeliveryRequest delivery,
            AwsSnsMessageBounceRequest bounce,
            AwsSnsComplaintRequest complaint) {
    }

    public static record AwsSnsMessageCommonHeadersRequest(List<String> from, String date, List<String> to,
            String messageId, String subject) {
    }

    public static record AwsSnsMessageDeliveryRequest(String timestamp,
            Integer processingTimeMillis,
            List<String> recipients,
            String smtpResponse,
            String remoteMtaIp,
            String reportingMTA) {
    }

    public static record AwsSnsMessageBounceRequest(String bounceType, String bounceSubType,
            List<AwsSnsBouncedRecipientsRequest> bouncedRecipients, String reportingMTA, String timestamp,
            String feedbackId, String remoteMtaIp) {
    }

    public static record AwsSnsBouncedRecipientsRequest(String status, String action, String diagnosticCode,
            String emailAddress) {
    }

    public static record AwsSnsComplaintRequest(String userAgent, List<AwsSnsComplainedRecipient> complainedRecipients,
            String complaintFeedbackType,
            String arrivalDate, String timestamp, String feedbackId) {
    }

    public static record AwsSnsComplainedRecipient(String emailAddress) {
    }

    public static record AwsSnsMessageHeaderRequest(String name, String value) {
    }

    @Getter
    @RequiredArgsConstructor
    public static enum AwsSnsNotificationTypeRequest {
        DELIVERY("Delivery"), BOUNCE("Bounce"), COMPLAINT("Complaint");

        private final String value;

        @JsonCreator
        public static AwsSnsNotificationTypeRequest factory(String value) {
            for (var e : values()) {
                if (e.getValue().equals(value)) {
                    return e;
                }
            }
            return null;
        }

    }
}
