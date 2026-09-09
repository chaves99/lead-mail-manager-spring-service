package cnpj.analyzr.lead;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record LeadRecord(Long id,
        String email,
        Integer send,
        LocalDate lastSend,
        Integer open,
        Integer click,
        LocalDate lastClickDate){}
