package cnpj.analyzr.statistics;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService service;

    @GetMapping("/track-open")
    public ResponseEntity<?> emailOpened(@RequestParam("campaign_row_id") Long campaignRowId) {
        log.info("statistics countAndRedirect - campaignId:{}", campaignRowId);
        service.opened(campaignRowId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/track-click")
    public ResponseEntity<?> trackClick(
            @RequestParam(name = "campaign_row_id", required = false) Long campaignRowId,
            @RequestParam(name = "url_target", required = false) String urlTarget) {
        log.info("trackClick - urlTarget:{}", urlTarget);
        service.trackClick(campaignRowId);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(urlTarget))
                .build();
    }

    // localhost:8080/statistics/track-click?campaign_row_id=131&url_target=https://itimenu.app/customer-menu/Q3DSSXnR0N82zC2GAn5P

}
