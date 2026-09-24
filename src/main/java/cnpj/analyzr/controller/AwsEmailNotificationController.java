package cnpj.analyzr.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/aws-ses-notification")
public class AwsEmailNotificationController {

    @PostMapping("/complaint")
    public ResponseEntity<?> post(@RequestBody String body) {
        log.info("post - body:{}", body);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bounce")
    public ResponseEntity<?> put(@RequestBody String body) {
        log.info("put - body:{}", body);
        return ResponseEntity.ok().build();
    }
}
