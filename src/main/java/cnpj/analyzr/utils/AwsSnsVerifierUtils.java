package cnpj.analyzr.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Scanner;

import cnpj.analyzr.payload.aws.AwsSnsRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record AwsSnsVerifierUtils() {

    public static void verify(String messageType, AwsSnsRequest msg) {
        log.info("verify - messageType:{} msg:{}", messageType, msg);
        if (msg.signatureVersion().equals("1")) {
            // Check the signature and throw an exception if the signature verification
            // fails.
            if (isMessageSignatureValid(msg)) {
                log.info(">>Signature verification succeeded");
            } else {
                log.info(">>Signature verification failed");
                throw new SecurityException("Signature verification failed.");
            }
        } else {
            log.info(">>Unexpected signature version. Unable to verify signature.");
            throw new SecurityException("Unexpected signature version. Unable to verify signature.");
        }

        // Process the message based on type.
        if (messageType.equals("Notification")) {
            // Do something with the Message and Subject.
            // Just log the subject (if it exists) and the message.
            String logMsgAndSubject = ">>Notification received from topic " + msg.topicArn();
            if (msg.subject() != null) {
                logMsgAndSubject += " Subject: " + msg.subject();
            }
            logMsgAndSubject += " Message: " + msg.message();
            log.info(logMsgAndSubject);
        } else if (messageType.equals("SubscriptionConfirmation")) {
            // You should make sure that this subscription is from the topic you expect.
            // Compare topicARN to your list of topics
            // that you want to enable to add this endpoint as a subscription.

            // Confirm the subscription by going to the subscribeURL location
            // and capture the return value (XML message body as a string
            try (Scanner sc = new Scanner(URI.create(msg.subscribeUrl()).toURL().openStream())) {
                StringBuilder sb = new StringBuilder();
                while (sc.hasNextLine()) {
                    sb.append(sc.nextLine());
                }
                log.info(">>Subscription confirmation (" + msg.subscribeUrl() + ") Return value: " + sb.toString());
                // Process the return value to ensure the endpoint is subscribed.
            } catch (IOException e) {
                log.error("verify - exception:", e);
            }
        } else if (messageType.equals("UnsubscribeConfirmation")) {
            // Handle UnsubscribeConfirmation message.
            // For example, take action if unsubscribing should not have occurred.
            // You can read the SubscribeURL from this message and
            // re-subscribe the endpoint.
            log.info(">>Unsubscribe confirmation: " + msg.message());
        } else {
            // Handle unknown message type.
            log.info(">>Unknown message type.");
        }
        log.info(">>Done processing message: " + msg.messageId());
    }

    private static boolean isMessageSignatureValid(AwsSnsRequest msg) {
        try {
            URI uri = URI.create(msg.signingCertUrl());
            URL url = uri.toURL();
            if (!"https".equals(uri.getScheme())) {
                throw new SecurityException("SigningCertURL was not using HTTPS: " + uri.toString());
            }

            log.info("verifyMessageSignatureURL - certURI(host):" + uri.getHost());
            // if (!endpoint.equals(certUri.getHost())) {
            //     throw new SecurityException(
            //             String.format("SigningCertUrl does not match expected endpoint. " +
            //                     "Expected %s but received endpoint was %s.",
            //                     endpoint, certUri.getHost()));
            //
            // }
        
            InputStream inStream = url.openStream();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);
            inStream.close();

            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(cert.getPublicKey());
            sig.update(getMessageBytesToSign(msg));
            return sig.verify(Base64.getDecoder().decode(msg.signature()));
        } catch (Exception e) {
            throw new SecurityException("Verify method failed.", e);
        }
    }

    private static byte[] getMessageBytesToSign(AwsSnsRequest msg) {
        byte[] bytesToSign = null;
        if (msg.type().equals("Notification"))
            bytesToSign = buildNotificationStringToSign(msg).getBytes();
        else if (msg.type().equals("SubscriptionConfirmation") || msg.type().equals("UnsubscribeConfirmation"))
            bytesToSign = buildSubscriptionStringToSign(msg).getBytes();
        return bytesToSign;
    }

    public static String buildNotificationStringToSign(AwsSnsRequest msg) {
        String stringToSign = null;

        // Build the string to sign from the values in the message.
        // Name and values separated by newline characters
        // The name value pairs are sorted by name
        // in byte sort order.
        stringToSign = "Message\n";
        stringToSign += msg.message() + "\n";
        stringToSign += "MessageId\n";
        stringToSign += msg.messageId() + "\n";
        if (msg.subject() != null) {
            stringToSign += "Subject\n";
            stringToSign += msg.subject() + "\n";
        }
        stringToSign += "Timestamp\n";
        stringToSign += msg.timestamp() + "\n";
        stringToSign += "TopicArn\n";
        stringToSign += msg.topicArn() + "\n";
        stringToSign += "Type\n";
        stringToSign += msg.type() + "\n";
        return stringToSign;
    }

    // Build the string to sign for SubscriptionConfirmation
    // and UnsubscribeConfirmation messages.
    public static String buildSubscriptionStringToSign(AwsSnsRequest msg) {
        String stringToSign = null;
        // Build the string to sign from the values in the message.
        // Name and values separated by newline characters
        // The name value pairs are sorted by name
        // in byte sort order.
        stringToSign = "Message\n";
        stringToSign += msg.message() + "\n";
        stringToSign += "MessageId\n";
        stringToSign += msg.messageId() + "\n";
        stringToSign += "SubscribeURL\n";
        stringToSign += msg.subscribeUrl() + "\n";
        stringToSign += "Timestamp\n";
        stringToSign += msg.timestamp() + "\n";
        stringToSign += "Token\n";
        stringToSign += msg.token() + "\n";
        stringToSign += "TopicArn\n";
        stringToSign += msg.topicArn() + "\n";
        stringToSign += "Type\n";
        stringToSign += msg.type() + "\n";
        return stringToSign;
    }
}
