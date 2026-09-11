package cnpj.analyzr.csv.processor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import cnpj.analyzr.lead.LeadRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LeadProcessor implements CsvProcessor<cnpj.analyzr.csv.processor.LeadProcessor.Lead> {

    private final LeadRepository leadRepository;

    public Optional<Lead> parse(String[] fields) {
        try {
            String cnae = fields[11].replace("\"", "");
            String email = fields[27].replace("\"", "");
            if (Stream.of(CNAES_FILTER).noneMatch(v -> v.equals(cnae))
                    || email == null || email.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(Lead.builder()
                    .email(email.toLowerCase())
                    .cnae(cnae)
                    .build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Builder
    public static record Lead(String email, String cnae) {
    }

    @Override
    public void insertAll(List<Lead> list) {
        if (!list.isEmpty())
            leadRepository.insert(list.stream().map(l -> l.email()).toList());
    }

    @Override
    public int getBatchSize() {
        return 5_000;
    }
}
