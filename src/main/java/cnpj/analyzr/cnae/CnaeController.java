package cnpj.analyzr.cnae;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cnae")
public class CnaeController {

    private final CnaeRepository cnaeRepository;

    @GetMapping
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(cnaeRepository.find());
    }

    @Builder
    public static record Cnae(int code, String description) {
    }
}
