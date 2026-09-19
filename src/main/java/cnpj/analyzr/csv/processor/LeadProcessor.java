package cnpj.analyzr.csv.processor;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import cnpj.analyzr.lead.LeadRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LeadProcessor implements CsvProcessor<cnpj.analyzr.csv.processor.LeadProcessor.InternalLeadProcessorRecord> {

    private final LeadRepository leadRepository;

    private static final Pattern patternRegex = Pattern.compile("^[a-zA-Z0-9._%+-]+@(hotmail|outlook|live|gmail|yahoo|msn)\\.com$");

    public Optional<InternalLeadProcessorRecord> parse(String[] fields) {
        try {
            String cnae = fields[11].replace("\"", "");
            String email = fields[27].replace("\"", "");
            if (Stream.of(CNAES_FILTER).noneMatch(v -> v.equals(cnae))
                    || email == null || email.isBlank()) {
                return Optional.empty();
            }
            if (!patternRegex.matcher(email).find()) {
                return Optional.empty();
            }
            return Optional.of(InternalLeadProcessorRecord.builder()
                    .email(email.toLowerCase())
                    .cnae(cnae)
                    .build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Builder
    public static record InternalLeadProcessorRecord(String email, String cnae) {
    }

    @Override
    public void insertAll(List<InternalLeadProcessorRecord> list) {
        if (!list.isEmpty())
            leadRepository.insert(list.stream().map(l -> l.email()).toList());
    }

    @Override
    public int getBatchSize() {
        return 5_000;
    }
}
