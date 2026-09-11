package cnpj.analyzr.csv;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("csv")
public class CsvController {

    @PostMapping
    public ResponseEntity<?> post(@RequestParam FileType fileType, @RequestBody byte[] request) {
        System.out.println(request);
        try (InputStream is = new ByteArrayInputStream(request);
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader bufferedReader = new BufferedReader(reader)) {
            System.out.println(bufferedReader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().build();
    }

    public static enum FileType {
        ESTABLISHMENT,
        LEAD,
        COMPANY;
    }
}
