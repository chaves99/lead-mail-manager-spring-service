package cnpj.analyzr.email;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

@Slf4j
@Service("awsSesEmailServiceImpl")
// @RequiredArgsConstructor
public class AwsSesEmailServiceImpl implements EmailServiceInterface {

    private final JavaMailSender javaMailSender;

    private String hostFrom;

    private String ownerEmail;

    public AwsSesEmailServiceImpl(JavaMailSender javaMailSender,
            @Value("${owner-email}") String ownerEmail,
            @Value("${mailgun.hostFrom}") String hostFrom) {
        this.javaMailSender = javaMailSender;
        this.hostFrom = hostFrom;
        this.ownerEmail = ownerEmail;
    }

    @Override
    public EmailResult send(String customerEmail, String subject, String body) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                    true,
                    StandardCharsets.UTF_8.name());
            helper.setTo(customerEmail);
            helper.setSubject(subject);
            helper.setFrom(hostFrom);
            helper.setText(body, true);

            javaMailSender.send(mimeMessage);
            log.info("send - email sent - recipient:{} from:{}", customerEmail, hostFrom);
            return new EmailResult(null, true, null);
        } catch (MessagingException e) {
            log.error("send - customerEmail:{} subject:{} exception: ", customerEmail, subject, e);
            return new EmailResult(e.getMessage(), false, null);
        } catch (AwsServiceException e) {
            log.error("send - customerEmail:{} subject:{} AwsServiceException: ", customerEmail, subject, e);
            throw e;
        }
    }

    @Override
    public EmailResult sendToOwner(String subject, String body) {
        log.info("sendToOwner");
        return send(ownerEmail, subject, body);
    }
}
