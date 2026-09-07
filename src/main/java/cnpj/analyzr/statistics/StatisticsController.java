package cnpj.analyzr.statistics;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping("statistics")
public class StatisticsController {

    @GetMapping
    public ResponseEntity<?> countAndRedirect(@RequestParam("campaign_row_id") Long campaignRowId) {
        log.info("statistics countAndRedirect - campaignId:{}", campaignRowId);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .build();
    }

}
