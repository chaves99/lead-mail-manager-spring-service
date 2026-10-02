package cnpj.analyzr.lead;

import java.net.URI;
import java.util.List;

import org.jsoup.HttpStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/unsubscribe/{leadId}/{rowId}")
    public ResponseEntity<?> unsubscribe(@PathVariable Long leadId, @PathVariable Long rowId) {
        try {
            service.unsubscribe(leadId, rowId);
        } catch (HttpStatusException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("https://itimenu.app/email-unsubscribe"))
                .build();
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
