package cnpj.analyzr.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

// {
//   "Type": "Notification",
//   "MessageId": "bfed3aed-1f2d-58be-8a05-59c277284fee",
//   "TopicArn": "arn:aws:sns:us-east-2:919989872457:sns-email-delivered-http-topic",
//   "Message": "{\"notificationType\":\"Delivery\",\"mail\":{\"timestamp\":\"2026-09-24T18:02:44.929Z\",\"source\":\"ItiMenu <support@itimenu.app>\",\"sourceArn\":\"arn:aws:ses:us-east-2:919989872457:identity/itimenu.app\",\"sourceIp\":\"189.63.235.168\",\"callerIdentity\":\"testing_email\",\"sendingAccountId\":\"919989872457\",\"messageId\":\"010f01a0d4958941-c6307468-7c7e-4be0-942e-07112a31e8f5-000000\",\"destination\":[\"viniciusbaleia1999@gmail.com\"],\"headersTruncated\":false,\"headers\":[{\"name\":\"Date\",\"value\":\"Thu, 24 Sep 2026 15:02:44 -0300 (GMT-03:00)\"},{\"name\":\"From\",\"value\":\"ItiMenu <support@itimenu.app>\"},{\"name\":\"To\",\"value\":\"viniciusbaleia1999@gmail.com\"},{\"name\":\"Message-ID\",\"value\":\"<978630095.5.1790272964306@vinicius-debian.vinicius>\"},{\"name\":\"Subject\",\"value\":\"Você está perdendo um aumento de 3% a 5% nas vendas?\"},{\"name\":\"MIME-Version\",\"value\":\"1.0\"},{\"name\":\"Content-Type\",\"value\":\"multipart/mixed;  boundary=\\\"----=_Part_3_1890527633.1790272964302\\\"\"}],\"commonHeaders\":{\"from\":[\"ItiMenu <support@itimenu.app>\"],\"date\":\"Thu, 24 Sep 2026 15:02:44 -0300 (GMT-03:00)\",\"to\":[\"viniciusbaleia1999@gmail.com\"],\"messageId\":\"<978630095.5.1790272964306@vinicius-debian.vinicius>\",\"subject\":\"Você está perdendo um aumento de 3% a 5% nas vendas?\"}},\"delivery\":{\"timestamp\":\"2026-09-24T18:02:45.784Z\",\"processingTimeMillis\":855,\"recipients\":[\"viniciusbaleia1999@gmail.com\"],\"smtpResponse\":\"250 2.0.0 OK  1790272965 46e09a7af769-81634c7df35si10452986a34.53 - gsmtp\",\"remoteMtaIp\":\"173.194.42.26\",\"reportingMTA\":\"e226-7.smtp-out.us-east-2.amazonses.com\"}}",
//   "Timestamp": "2026-09-24T18:02:45.868Z",
//   "SignatureVersion": "1",
//   "Signature": "Uiat85vu1iwsqBpAVR+ACkm7e6YYwZ7kjFDaegCexDbJjmBDdoEz+5F2BGKosvV4YllUh5ATpC5EYJsPdzivuoYWMy54xvwdyFFLIXv9AuRJXuM3+BBYJCD9ERohLlEcW4Zwayr6WvCmCz6NBm1wMuWdBIFfkrVn/6bYW0PF2FqU2yw+gl/7NZq5cX6v3eygMSy9TtewB3TJXn8TvO/VrK3wD1MP5/rVWxNmL0Fh/IZZ+S+Ns9SrE7B9r4LGGl2792IBs2aywQ6/WlxFXWlyvpQdFv5zlV80hwjCFaCcsBtbGvF7tfjdQx624gt1SbKq0yE1S29uCBVjSsd7LFqubA==",
//   "SigningCertURL": "https://sns.us-east-2.amazonaws.com/SimpleNotificationService-1e59c4574facfe41babdb2d652f8ebef.pem",
//   "UnsubscribeURL": "https://sns.us-east-2.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:us-east-2:919989872457:sns-email-delivered-http-topic:9852ab44-aae8-455b-9287-b5f46aec9bca"
// }
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
