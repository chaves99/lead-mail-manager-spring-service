package cnpj.analyzr.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.awspring.cloud.sns.annotation.endpoint.NotificationMessageMapping;
import io.awspring.cloud.sns.annotation.handlers.NotificationMessage;
import io.awspring.cloud.sns.annotation.handlers.NotificationSubject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/aws-ses-notification")
public class AwsEmailNotificationController {

    @PostMapping("/complaint")
    public ResponseEntity<?> complaing(@RequestHeader HttpHeaders headers, @RequestBody String body) {
        log.info("POST complaint - headers:{} body:{}", headers.toString(), body);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bounce")
    public ResponseEntity<?> bounce(@RequestHeader HttpHeaders headers, @RequestBody String body,
            @NotificationSubject String subject, @NotificationMessage String message) {
        log.info("POST bounce - headers:{} body:{}", headers.toString(), body);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delivered")
    @NotificationMessageMapping
    public ResponseEntity<?> delivered(@RequestHeader HttpHeaders headers, @RequestBody String body,
            @NotificationSubject String subject, @NotificationMessage String message) {
        log.info("POST delivered - headers:{} body:{}", headers.toString(), body);
        log.info("POST delivered - subject:{} message:{}", subject, message);
        return ResponseEntity.ok().build();
    }
}
