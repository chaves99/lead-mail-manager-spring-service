package cnpj.analyzr.email;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import cnpj.analyzr.payload.aws.AwsSesHeaderRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("awsSesEmailServiceImpl")
// @RequiredArgsConstructor
public class AwsSesEmailServiceImpl {

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

    public void send(String customerEmail, String subject, String body, AwsSesHeaderRequest header)
            throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                true,
                StandardCharsets.UTF_8.name());
        helper.setTo(customerEmail);
        helper.setSubject(subject);
        helper.setFrom(hostFrom);
        helper.setText(body, true);

        if (header != null) {
            mimeMessage.addHeader("X-SES-MESSAGE-TAGS", header.toString());
        }
        javaMailSender.send(mimeMessage);
        log.info("send - email sent - recipient:{} from:{}", customerEmail, hostFrom);
    }

    public void sendToOwner(String subject, String body) throws MessagingException {
        log.info("sendToOwner");
        send(ownerEmail, subject, body, null);
    }
}
