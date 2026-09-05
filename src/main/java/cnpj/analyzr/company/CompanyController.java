package cnpj.analyzr.company;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cnpj.analyzr.payload.GetCompanyRequestFilter;
import cnpj.analyzr.payload.GetEmailRequestFilter;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final CompanyRepository repository;

    @PostMapping
    public ResponseEntity<CompanyResponse> getFilter(@RequestBody GetCompanyRequestFilter filter) {
        List<Company> companies = repository.find(filter);
        int count = repository.count(filter);
        return ResponseEntity.ok(new CompanyResponse(companies, count));
    }

    @PostMapping("/email")
    public ResponseEntity<EmailCompanyResponseContainer> getEmails(@RequestBody GetEmailRequestFilter filter) {
        System.out.println("getEmails: filter: " + filter);
        List<EmailCompanyResponse> groupByEmail = repository.groupByEmail(filter);
        int countGroupEmail = repository.countGroupEmail(filter);
        return ResponseEntity.ok(new EmailCompanyResponseContainer(groupByEmail, countGroupEmail));
    }

    @DeleteMapping("/email/{email}")
    public ResponseEntity<?> deleteByEmail(@PathVariable String email) {
        repository.deleteByEmail(email);
        return ResponseEntity.ok().build();
    }

    public static record CompanyResponse(List<Company> companies, int total) {
    }

    @Builder
    public static record EmailCompanyResponseContainer(List<EmailCompanyResponse> list, int total) {
    }

    @Builder
    public static record EmailCompanyResponse(String email, int count) {
    }
}
