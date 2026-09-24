package cnpj.analyzr.payload.aws;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AwsSnsRequest(
        @JsonProperty("Type") String type,
        @JsonProperty("Token") String token,
        @JsonProperty("MessageId") String messageId,
        @JsonProperty("TopicArn") String topicArn,
        @JsonProperty("Message") String message,
        @JsonProperty("Timestamp") String timestamp,
        @JsonProperty("SignatureVersion") String signatureVersion,
        @JsonProperty("Signature") String signature,
        @JsonProperty("SigningCertURL") String signingCertUrl,
        @JsonProperty("Subject") String subject,
        @JsonProperty("UnsubscribeURL") String unsubscribeUrl,
        @JsonProperty("SubscribeURL") String subscribeUrl) {
}
