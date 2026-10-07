package cnpj.analyzr.csv;

import org.springframework.http.ResponseEntity;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("csv")
public class CsvController {

    private final CsvService csvService;

    @PostMapping
    public ResponseEntity<?> post(@RequestBody byte[] request) {
        DataSize dataSize = DataSize.ofBytes(request.length);
        log.info("POST - file size: (GB):{} (MB):{} (KB):{}", dataSize.toGigabytes(), dataSize.toMegabytes(), dataSize.toKilobytes());
        csvService.processAsync(request);
        return ResponseEntity.ok().build();
    }
}
