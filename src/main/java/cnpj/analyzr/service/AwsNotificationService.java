package cnpj.analyzr.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import cnpj.analyzr.campaign.row.CampaignRow;
import cnpj.analyzr.campaign.row.CampaignRowRepository;
import cnpj.analyzr.lead.LeadRecord;
import cnpj.analyzr.lead.LeadRepository;
import cnpj.analyzr.payload.aws.AwsSesHeaderRequest;
import cnpj.analyzr.payload.aws.AwsSnsMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwsNotificationService {

    private final LeadRepository leadRepository;
    private final CampaignRowRepository campaignRowRepository;

    public void process(AwsSnsMessageRequest message) {
        switch (message.notificationType()) {
            case BOUNCE:
                this.updateStatus(message, CampaignRow.Status.BOUNCED);
                break;
            case COMPLAINT:
                this.updateStatus(message, CampaignRow.Status.COMPLAINED);
                break;
            case DELIVERY:
                this.updateStatus(message, CampaignRow.Status.DELIVERED);
                break;
            default:
                log.warn("process - not handled message type:{}", message);
        }
    }

    public Optional<LeadRecord> findLead(AwsSnsMessageRequest message) {
        String first = message.mail().destination().getFirst();
        if (first != null) {
            return leadRepository.find(first);
        }
        return Optional.empty();
    }

    private Optional<CampaignRow> getCampaignRowId(AwsSnsMessageRequest message) {
        return message.mail().headers()
                .stream()
                .filter(header -> header.name().equals("X-SES-MESSAGE-TAGS"))
                .findAny()
                .map(header -> {
                    if (header != null && header.value() != null) {
                        Optional<AwsSesHeaderRequest> sesHeaderRequest = AwsSesHeaderRequest.fromHeader(header.value());
                        return sesHeaderRequest.isPresent()
                                ? sesHeaderRequest.get().campaignRowId()
                                : null;
                    }
                    return null;
                })
                .flatMap(campaignRowId -> campaignRowRepository.findById(campaignRowId));
    }

    private void updateStatus(AwsSnsMessageRequest message, CampaignRow.Status status) {
        getCampaignRowId(message).ifPresentOrElse(campaignRow -> {
            campaignRowRepository.updateEmailStatus(campaignRow.id(), status);
            leadRepository
                    .find(campaignRow.leadId())
                    .ifPresentOrElse(lead -> {
                        int count = lead.send() != null ? lead.send() + 1 : 1;
                        leadRepository.updatePlusCounter(lead.id(), count);

                        if (status.equals(CampaignRow.Status.BOUNCED)
                                || status.equals(CampaignRow.Status.COMPLAINED)) {
                            log.info("updateStatus - marking as unrechable - status:{} email:{}", status, lead.email());
                            leadRepository.updateUnrechable(lead.id());
                        }
                    }, () -> log.warn("process - lead not found"));
        }, () -> log.warn("updateStatus - campaing row not found - message:{} status:{}", message, status));
    }
}
