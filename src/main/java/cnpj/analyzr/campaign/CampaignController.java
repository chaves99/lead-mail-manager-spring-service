package cnpj.analyzr.campaign;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cnpj.analyzr.campaign.entity.req.CampaignExecuteRecordRequest;
import cnpj.analyzr.campaign.entity.res.CampaignDetailsRecordResponse;
import cnpj.analyzr.campaign.entity.res.CampaignRecordResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("campaign")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignRepository repository;
    private final CampaignService campaignService;

    @GetMapping
    public ResponseEntity<List<CampaignRecordResponse>> get() {
        return ResponseEntity.ok(repository.find());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignDetailsRecordResponse> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getDetails(id));
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody CampaignExecuteRecordRequest body) {
        this.campaignService.execute(body);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/undo")
    public ResponseEntity<?> undo(@PathVariable Long id) {
        this.campaignService.undo(id);
        return ResponseEntity.ok().build();
    }

}
