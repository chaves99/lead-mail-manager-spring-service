package cnpj.analyzr.csv;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("csv")
public class CsvController {

    @PostMapping
    public ResponseEntity<?> post(HttpServletRequest request) {
        System.out.println(request.getContentLength());
        System.out.println(request.getContentType());
        System.out.println(request.getContextPath());
        // System.out.println("-> file size:" + file.getSize() + " file:" + file.getOriginalFilename());
        return ResponseEntity.ok().build();
    }
}
