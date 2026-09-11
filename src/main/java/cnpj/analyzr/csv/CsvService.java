package cnpj.analyzr.csv;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import cnpj.analyzr.csv.CsvController.FileType;
import cnpj.analyzr.csv.processor.CsvProcessor;
import cnpj.analyzr.csv.processor.EstablishmentProcessor;
import cnpj.analyzr.csv.processor.LeadProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvService {

    private final LeadProcessor leadProcessor;
    private final EstablishmentProcessor establishmentProcessor;

    @Async
    public void processAsync(FileType fileType, byte[] file) {
        long startTime = System.currentTimeMillis();
        try (InputStream is = new ByteArrayInputStream(file);
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            @SuppressWarnings("unchecked")
            CsvProcessor<Object> processor = getProcessor(fileType);
            List<Object> bulkObjectList = new ArrayList<>();
            log.info("processAsync - start to read file type:{} with processor:{}", fileType, processor);
            int c = 0;
            while ((line = bufferedReader.readLine()) != null) {
                processor.parse(line.split(";")).ifPresent(bulkObjectList::add);
                if (bulkObjectList.size() == processor.getBatchSize()) {
                    log.info("inserting bulk - lines read:{}", String.format("%,d", c));
                    processor.insertAll(bulkObjectList);
                    bulkObjectList.clear();
                }
                c++;
            }
            processor.insertAll(bulkObjectList); // inserting remaning
        } catch (Exception e) {
            log.error("process - fileType:{} exception:{}", fileType, e.getMessage());
        }
        long endTime = System.currentTimeMillis();
        log.info("processAsync - processed file in {} seconds", ((endTime - startTime) / 1000));
    }

    @SuppressWarnings("rawtypes")
    private CsvProcessor getProcessor(FileType fileType) {
        return switch (fileType) {
            case ESTABLISHMENT -> establishmentProcessor;
            case LEAD -> leadProcessor;

            default -> throw new IllegalArgumentException();
        };
    }

}
