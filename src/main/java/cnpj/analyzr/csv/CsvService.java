package cnpj.analyzr.csv;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import cnpj.analyzr.lead.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvService {

    private final LeadRepository leadRepository;

    @Async
    public void processAsync(byte[] file) {
        long startTime = System.currentTimeMillis();
        try (InputStream is = new ByteArrayInputStream(file);
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader bufferedReader = new BufferedReader(reader)) {
            List<String> emails = new ArrayList<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line != null && !line.isBlank()) {
                    emails.add(line);
                }
            }
            leadRepository.insert(emails);
            log.info("processAsync - inserted {} rows", emails.size());
        } catch (Exception e) {
            log.error("process - exception:{}", e.getMessage());
        }
        long endTime = System.currentTimeMillis();
        log.info("processAsync - processed file in {} seconds", ((endTime - startTime) / 1000));
    }
}
