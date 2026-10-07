package cnpj.analyzr.lead;

import java.net.URI;
import java.util.List;

import org.jsoup.HttpStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/lead")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService service;

    @PostMapping("/search")
    public ResponseEntity<LeadResponse> fetch(@RequestBody LeadFilterRecord filter) {
        return ResponseEntity.ok(service.fetch(filter));
    }

    @PostMapping()
    public ResponseEntity<LeadResponse> add(@RequestBody LeadCreationRequest lead) {
        if (lead.email() == null) {
            return ResponseEntity.badRequest().build();
        }
        service.create(lead.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<LeadResponse> addBatch(@RequestBody LeadCreationBatchRequest lead) {
        if (lead.emails() == null) {
            return ResponseEntity.badRequest().build();
        }
        service.createBatch(lead.emails());
        return ResponseEntity.noContent().build();
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
        } catch(Exception e) {
            e.printStackTrace();
            log.error("delete - exception: ", e);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }

    public static record LeadTotalDashboard(Integer registered, Integer sent,
            Integer opened, Integer clicked, Integer unsubscribed, Integer unreachable) {
        public LeadTotalDashboard withRegistered(Integer registered) {
            return new LeadTotalDashboard(registered, sent, opened, clicked, unsubscribed, unreachable);
        }
    }

    public static record LeadResponse(Integer total, List<LeadRecord> list) {
    }

    public static record LeadCreationRequest(String email) {
    }

    public static record LeadCreationBatchRequest(List<String> emails) {
    }
}
