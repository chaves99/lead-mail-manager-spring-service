package cnpj.analyzr.statistics;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService service;

    @GetMapping("/track-open")
    public ResponseEntity<?> emailOpened(@RequestHeader Map<String, String> headers,
            @RequestParam(value = "campaign_row_id", required = false) Long campaignRowId) {
        ResponseEntity<Object> responseEntity = ResponseEntity
                .status(HttpStatus.OK)
                .build();
        if (campaignRowId == null) {
            return responseEntity;
        }
        service.trackOpen(campaignRowId);
        return responseEntity;
    }

    @GetMapping("/track-click")
    public ResponseEntity<?> trackClick(@RequestHeader Map<String, String> headers,
            @RequestParam(name = "campaign_row_id", required = false) Long campaignRowId,
            @RequestParam(name = "url_target", required = false) String urlTarget) {
        if (campaignRowId == null || urlTarget == null) {
            return ResponseEntity.ok().build();
        }
        service.trackClick(campaignRowId);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(urlTarget))
                .build();
    }

}
