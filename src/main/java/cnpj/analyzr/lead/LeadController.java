package cnpj.analyzr.lead;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/lead")
@RequiredArgsConstructor
@Slf4j
public class LeadController {

    private final LeadRepository leadRepository;

    @PostMapping
    public ResponseEntity<LeadResponse> fetch(@RequestBody LeadFilterRecord filter) {
        log.info("fetch - filter:{}", filter);
        List<LeadRecord> body = leadRepository.find(filter);
        int count = leadRepository.count(filter);
        return ResponseEntity.ok(new LeadResponse(count, body));
    }

    public static record LeadResponse(Integer total, List<LeadRecord> list) {
    }
}
