package cnpj.analyzr.email;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;

import cnpj.analyzr.template.TemplateRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    @Value("${owner-email}")
    private String ownerEmail;

    @Value("${mailgun.hostFrom}")
    private String hostFrom;

    private final RestClient restClient;

    public EmailService(RestClient.Builder restClientBuilder,
            @Value("${mailgun.url}") String mailgunUrl,
            @Value("${mailgun.apikey}") String apiKey,
            TemplateRepository templateRepository) {
        log.info("constructor - mailgunUrl:{}", mailgunUrl);
        String basicAuth = "api:" + apiKey;
        this.restClient = restClientBuilder
                .baseUrl(mailgunUrl)
                .defaultHeaders(h -> {
                    h.add("Content-Type", "multipart/form-data");
                    h.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(basicAuth.getBytes()));
                })
                .build();
    }

    @SuppressWarnings("unchecked")
    public EmailResult send(String customerEmail, String subject, String body) {
        try {
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
            parts.add("from", hostFrom);
            parts.add("to", customerEmail);
            parts.add("subject", subject);
            parts.add("html", body);
            ResponseSpec res = restClient.post().body(parts).retrieve();
            // log.info("send email:{} response:{}", customerEmail, res.body(String.class));
            return new EmailResult(null, true);
        } catch (HttpClientErrorException e) {
            Map<String, Object> bodyMap = e.getResponseBodyAs(HashMap.class);
            log.warn("send - message: {}", e.getResponseBodyAsString());
            return new EmailResult((String) bodyMap.get("message"), false);
        } catch (Exception e) {
            log.info("send - error: ", e);
            return new EmailResult(e.getMessage(), false);
        }
    }

    public EmailResult sendToOwner(String subject, String body) {
        log.info("sendToOwner");
        return send(ownerEmail, subject, body);
    }

    public static record EmailResult(String message, boolean success) {
    }
}
