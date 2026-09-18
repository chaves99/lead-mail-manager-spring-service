package cnpj.analyzr.lead;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/lead")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService service;

    @PostMapping
    public ResponseEntity<LeadResponse> fetch(@RequestBody LeadFilterRecord filter) {
        return ResponseEntity.ok(service.fetch(filter));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<LeadTotalDashboard> getTotalDashboard() {
        return ResponseEntity.ok(service.getTotalDashboard());
    }

    @PutMapping("/{campaignRowId}/unsubscribe")
    public ResponseEntity<?> unsubscribe(@PathVariable Long campaignRowId) {
        service.unsubscribe(campaignRowId);
        return ResponseEntity.ok().build();
    }

    public static record LeadTotalDashboard(Integer registered, Integer sent,
            Integer opened, Integer clicked, Integer unsubscribed) {
        public LeadTotalDashboard withRegistered(Integer registered) {
            return new LeadTotalDashboard(registered, sent, opened, clicked, unsubscribed);
        }
    }

    public static record LeadResponse(Integer total, List<LeadRecord> list) {
    }
}
