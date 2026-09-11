package cnpj.analyzr.establishment;

import java.util.List;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EstablishmentRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void insert(List<Establishment> list) {
        String sql = """
                INSERT INTO establishment(
                        basic_cnpj, order_cnpj, cnpj_verification_digit, mother_branch_identifier, fantasy_name,
                        registry_situation, registry_situation_date, reason_registry_situation, outdoor_city_name, country,
                        start_activity_date, cnae_tax_primary, cnae_tax_secondary, address_street_type, address_street,
                        address_number, address_complement, address_neighborhood, address_code, address_state,
                        address_city, telephone_code1, telephone1, telephone_code2, telephone2,
                        special_situation, special_situation_date, lead_id)
                VALUES(?, ?, ?, ?, ?,
                       ?, ?, ?, ?, ?,
                       ?, ?, ?, ?, ?,
                       ?, ?, ?, ?, ?,
                       ?, ?, ?, ?, ?,
                       ?, ?, ?)
                ON CONFLICT (basic_cnpj, order_cnpj, cnpj_verification_digit) DO NOTHING
                """;

        jdbcTemplate.getJdbcTemplate().batchUpdate(sql, list, list.size(), (ps, establishment) -> {
            int idx = 1;
            ps.setLong(idx++, Long.parseLong(establishment.cnpjBasic()));
            ps.setInt(idx++, Integer.parseInt(establishment.cnpjOrder()));
            ps.setInt(idx++, Integer.parseInt(establishment.cnpjVerification()));
            ps.setString(idx++, establishment.motherBranchIdentifier());
            ps.setString(idx++, establishment.fantasyName());
            ps.setString(idx++, establishment.registrySituation());
            ps.setString(idx++, establishment.registrySituationDate());
            ps.setString(idx++, establishment.reasonRegistrySituation());
            ps.setString(idx++, establishment.outdoorCityName());
            ps.setString(idx++, establishment.country());
            ps.setString(idx++, establishment.startActivityDate());
            ps.setString(idx++, establishment.cnaeTaxPrimary());
            ps.setString(idx++, establishment.cnaeTaxSecondary());
            ps.setString(idx++, establishment.addressStreetType());
            ps.setString(idx++, establishment.addressStreet());
            ps.setString(idx++, establishment.addressNumber());
            ps.setString(idx++, establishment.addressComplement());
            ps.setString(idx++, establishment.addressNeighborhood());
            ps.setString(idx++, establishment.addressCode());
            ps.setString(idx++, establishment.addressState());
            ps.setString(idx++, establishment.addressCity());
            ps.setString(idx++, establishment.telephoneCode1());
            ps.setString(idx++, establishment.telephone1());
            ps.setString(idx++, establishment.telephoneCode2());
            ps.setString(idx++, establishment.telephone2());
            ps.setString(idx++, establishment.specialSituation());
            ps.setString(idx++, establishment.specialSituationDate());
            ps.setLong(idx++, establishment.leadId());
        });
    }

    @Builder
    public record Establishment(
            String cnpjBasic,
            String cnpjOrder,
            String cnpjVerification,
            String motherBranchIdentifier,
            String fantasyName,
            String registrySituation,
            String registrySituationDate,
            String reasonRegistrySituation,
            String outdoorCityName,
            String country,
            String startActivityDate,
            String cnaeTaxPrimary,
            String cnaeTaxSecondary,
            String addressStreetType,
            String addressStreet,
            String addressNumber,
            String addressComplement,
            String addressNeighborhood,
            String addressCode,
            String addressState,
            String addressCity,
            String telephoneCode1,
            String telephone1,
            String telephoneCode2,
            String telephone2,
            String email,
            String specialSituation,
            String specialSituationDate,
            Long leadId) {

        public enum RegistrySituation {
            NULL(),
            ACTIVE(),
            SUSPENDED(),
            UNABLE(),
            DOWNLOADED();
        }

        public String getCnpj() {
            return cnpjBasic + cnpjOrder + cnpjVerification;
        }
    }
}
