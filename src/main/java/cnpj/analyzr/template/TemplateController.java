package cnpj.analyzr.template;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cnpj.analyzr.email.EmailService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/template")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateRepository repository;

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<Template> create(@RequestBody Template body) {
        if (body.body() == null || body.body().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (body.subject() == null || body.subject().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (body.name() == null || body.name().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (body.id() != null && body.id() > 0) {
            repository.update(body);
        } else {
            repository.insert(body);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Template>> get() {
        return ResponseEntity.ok(repository.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        repository.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test/{id}")
    public ResponseEntity<?> test(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }

        return repository.find(id)
                .map(t -> {
                    emailService.sendToOwner(t.subject(), t.body());
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());

    }
}
