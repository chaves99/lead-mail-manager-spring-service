package cnpj.analyzr.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cnpj.analyzr.payload.aws.AwsSnsMessageRequest;
import cnpj.analyzr.payload.aws.AwsSnsRequest;
import cnpj.analyzr.service.AwsNotificationService;
import cnpj.analyzr.utils.AwsSnsVerifierUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/aws-ses-notification")
public class AwsEmailNotificationController {

    private final ObjectMapper objectMapper;

    private final AwsNotificationService awsNotificationService;

    @PostMapping("/complaint")
    public ResponseEntity<?> complain(@RequestHeader HttpHeaders headers, @RequestBody(required = false) String body) {
        log.info("POST complaint - headers:{} body:{}", body);
        handleNotification(headers, body);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bounce")
    public ResponseEntity<?> bounce(@RequestHeader HttpHeaders headers, @RequestBody(required = false) String body) {
        log.info("POST bounce - headers:{} body:{}", body);
        handleNotification(headers, body);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delivered")
    public ResponseEntity<?> delivered(@RequestHeader HttpHeaders headers, @RequestBody(required = false) String body) {
        handleNotification(headers, body);
        return ResponseEntity.ok().build();
    }

    private void handleNotification(HttpHeaders headers, String body) {
        try {
            if (body == null) {
                return;
            }
            String headerMessageType = headers.getFirst("x-amz-sns-message-type");
            AwsSnsRequest value = objectMapper.readValue(body, AwsSnsRequest.class);
            AwsSnsVerifierUtils.verify(headerMessageType, value);
            AwsSnsMessageRequest message = objectMapper.readValue(value.message(), AwsSnsMessageRequest.class);
            awsNotificationService.process(message);
        } catch (Exception e) {
            log.error("handleNotification - error: ", e);
        }
    }
}
