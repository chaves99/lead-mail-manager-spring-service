package cnpj.analyzr.csv.processor;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import cnpj.analyzr.establishment.EstablishmentRepository;
import cnpj.analyzr.establishment.EstablishmentRepository.Establishment;
import cnpj.analyzr.lead.LeadRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EstablishmentProcessor implements CsvProcessor<Establishment> {

    private final EstablishmentRepository repository;
    private final LeadRepository leadRepository;

    @Override
    public Optional<Establishment> parse(String[] fields) {
        try {
            String email = fields[27].replace("\"", "").toLowerCase();
            if (email.isBlank()) {
                return Optional.empty();
            }
            Optional<Long> lead = findLead(email);
            if (lead.isEmpty()) {
                return Optional.empty();
            }
            Establishment establishment = Establishment.builder()
                    .cnpjBasic(fields[0].replace("\"", ""))
                    .cnpjOrder(fields[1].replace("\"", ""))
                    .cnpjVerification(fields[2].replace("\"", ""))
                    .motherBranchIdentifier(fields[3].replace("\"", ""))
                    .fantasyName(fields[4].replace("\"", ""))
                    .registrySituation(fields[5].replace("\"", ""))
                    .registrySituationDate(fields[6].replace("\"", ""))
                    .reasonRegistrySituation(fields[7].replace("\"", ""))
                    .outdoorCityName(fields[8].replace("\"", ""))
                    .country(fields[9].replace("\"", ""))
                    .startActivityDate(fields[10].replace("\"", ""))
                    .cnaeTaxPrimary(fields[11].replace("\"", ""))
                    .cnaeTaxSecondary(fields[12].replace("\"", ""))
                    .addressStreetType(fields[13].replace("\"", ""))
                    .addressStreet(fields[14].replace("\"", ""))
                    .addressNumber(fields[15].replace("\"", ""))
                    .addressComplement(fields[16].replace("\"", ""))
                    .addressNeighborhood(fields[17].replace("\"", ""))
                    .addressCode(fields[18].replace("\"", ""))
                    .addressState(fields[19].replace("\"", ""))
                    .addressCity(fields[20].replace("\"", ""))
                    .telephoneCode1(fields[21].replace("\"", ""))
                    .telephone1(fields[22].replace("\"", ""))
                    .telephoneCode2(fields[23].replace("\"", ""))
                    .telephone2(fields[24].replace("\"", ""))
                    .email(email)
                    .specialSituation(fields[28].replace("\"", ""))
                    .specialSituationDate(fields[29].replace("\"", ""))
                    .leadId(lead.get())
                    .build();
            return Optional.of(establishment);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Long> findLead(String email) {
        return leadRepository.find(email);
    }

    @Override
    public void insertAll(List<Establishment> list) {
        repository.insert(list);
    }

    @Override
    public int getBatchSize() {
        return 5_000;
    }
}
