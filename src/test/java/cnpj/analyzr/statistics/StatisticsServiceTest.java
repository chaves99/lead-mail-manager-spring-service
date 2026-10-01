package cnpj.analyzr.statistics;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import cnpj.analyzr.campaign.row.CampaignRow;
import cnpj.analyzr.campaign.row.CampaignRowRepository;
import cnpj.analyzr.lead.LeadRepository;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceTest {

    @InjectMocks
    StatisticsService service;

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private CampaignRowRepository campaignRowRepository;

    @Test
    public void trackOpen_ShouldNotTrack() {
        CampaignRow campaignRow = CampaignRow.builder()
                .id(123l)
                .leadId(456l)
                .sendDate(LocalDateTime.now().minusMinutes(1))
                .build();

        when(campaignRowRepository.findById(123l))
                .thenReturn(Optional.of(campaignRow));

        service.trackOpen(123l);

        verify(campaignRowRepository, never()).updateOpen(Mockito.anyLong());
    }

    @Test
    public void trackOpen_ShouldTrack() {
        CampaignRow campaignRow = CampaignRow.builder()
                .id(123l)
                .leadId(456l)
                .sendDate(LocalDateTime.now().minusMinutes(5))
                .build();

        when(campaignRowRepository.findById(123l))
                .thenReturn(Optional.of(campaignRow));

        service.trackOpen(123l);

        verify(campaignRowRepository, times(1)).updateOpen(Mockito.anyLong());
    }
}
