package cnpj.analyzr.campaign;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cnpj.analyzr.lead.LeadFilterRecord;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("campaign")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignRepository repository;
    private final CampaignService campaignService;

    @GetMapping
    public ResponseEntity<List<CampaignResponse>> get() {
        return ResponseEntity.ok(repository.find());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        return null;
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody CampaignExecute body) {
        this.campaignService.execute(body);
        return ResponseEntity.ok().build();
    }

    @Builder
    public static record CampaignResponse(Long id, String description,
            Long templateId, String templateName, String fantayName,
            Integer rowCount, LocalDateTime createdAt, Integer success, Integer failure) {
    }

    public static record CampaignExecute(LeadFilterRecord filter, String description, Long templateId, Integer limit) {
    }
}
