package cnpj.analyzr.company;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import cnpj.analyzr.company.CompanyController.EmailCompanyResponse;
import cnpj.analyzr.payload.GetCompanyRequestFilter;
import cnpj.analyzr.payload.GetCompanyRequestFilter.CnaeRequestFilter;
import cnpj.analyzr.payload.GetEmailRequestFilter;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompanyRepository {

    private static final int DEFAULT_LIMIT = 100;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Company> findByEmails(Collection<String> emails) {
        String sql = "SELECT * FROM company WHERE email in (:emails)";
        
        return executeQuery(sql, Map.of("emails", emails));
    }

    public List<Company> find(List<Long> ids) {
        String sql = """
                SELECT * FROM company WHERE id in (:ids)
                """;

        Map<String, Object> map = new HashMap<>();
        map.put("ids", ids);

        return executeQuery(sql, map);
    }

    private List<Company> executeQuery(String sql, Map<String, Object> map) {
        return jdbcTemplate.query(sql, map, (rs, rowNum) -> {
            return Company.builder()
                    .id(rs.getLong("id"))
                    .cnpj(rs.getLong("cnpj"))
                    .fantasyName(rs.getString("fantasy_name"))
                    .email(rs.getString("email"))
                    .build();
        });
    }

    public Optional<Company> find(String email) {
        String sql = """
                SELECT * FROM company WHERE email = :email
                """;

        Map<String, Object> map = new HashMap<>();
        map.put("email", email);

        return Optional.of(jdbcTemplate.queryForObject(sql, map, (rs, rowNum) -> {
            return Company.builder()
                    .id(rs.getLong("id"))
                    .cnpj(rs.getLong("cnpj"))
                    .fantasyName(rs.getString("fantasy_name"))
                    .email(rs.getString("email"))
                    .build();
        }));
    }

    public int countGroupEmail(GetEmailRequestFilter filter) {
        StringBuilder query = new StringBuilder("""
                SELECT COUNT(*) FROM (SELECT DISTINCT c.email as counter FROM company c
                LEFT JOIN campaign_row cr ON (c.id = cr.company_id)
                WHERE 1=1
                """);

        createWhere(filter, query);

        query.append(" GROUP BY c.email ");
        if (filter.getCompanyPerEmail() != null && filter.getCompanyPerEmail() > 0) {
            query.append(" HAVING COUNT(c.email) =  " + filter.getCompanyPerEmail());
        }
        query.append(")");

        return jdbcTemplate.queryForObject(query.toString(), Map.of(), Integer.class);
    }

    public List<EmailCompanyResponse> groupByEmail(GetEmailRequestFilter filter) {
        StringBuilder query = new StringBuilder("""
                SELECT c.email as email, COUNT(c.email) as counter FROM company c
                LEFT JOIN campaign_row cr ON (c.id = cr.company_id)
                WHERE 1=1
                """);

        createWhere(filter, query);

        query.append(" GROUP BY c.email ");

        if (filter.getCompanyPerEmail() != null && filter.getCompanyPerEmail() > 0) {
            query.append(" HAVING COUNT(c.email) =  " + filter.getCompanyPerEmail());
        }

        query.append(" ORDER BY c.email LIMIT " + filter.getPageSize());

        return jdbcTemplate.query(query.toString(), Map.of(), (rs, rowNum) -> {
            return EmailCompanyResponse.builder()
                    .email(rs.getString("email"))
                    .count(rs.getInt("counter"))
                    .build();
        });
    }

    public List<Company> find(GetCompanyRequestFilter filter) {
        StringBuilder query = new StringBuilder("""
                SELECT c.id, c.cnpj, c.fantasy_name, c.email
                FROM company c
                LEFT JOIN campaign_row cr
                ON (c.id = cr.company_id)
                """);

        if (filter.getLastCampaignBefore() != null) {
            query.append(" JOIN campaign camp ON (camp.id = cr.campaign_parent_id) ");
        }

        query.append(" WHERE 1=1 ");

        createWhere(filter, query);

        Map<String, Object> map = new HashMap<>();

        if (filter.getLastCampaignBefore() != null) {
            query.append("AND camp.created_at > :lastDate");
            map.put("lastDate", filter.getLastCampaignBefore().toString());
        }

        // LAST INDEX FILTER
        if (filter.getLastIndex() != null && filter.getLastIndex() > 0) {
            query.append(" AND c.id > :lastIndex ");
            map.put("lastIndex", filter.getLastIndex());
        }

        // ADD LIMIT
        map.put("limit", filter.getPageSize() == 0 ? DEFAULT_LIMIT : filter.getPageSize());
        query.append("ORDER BY c.id LIMIT :limit");

        System.out.println("find - query:" + query);
        return jdbcTemplate.query(query.toString(), map, (rs, rowNum) -> {
            return Company.builder()
                    .id(rs.getLong("id"))
                    .cnpj(rs.getLong("cnpj"))
                    .fantasyName(rs.getString("fantasy_name"))
                    .email(rs.getString("email"))
                    .build();
        });
    }

    public int count(GetCompanyRequestFilter filter) {
        StringBuilder countQuery = new StringBuilder("""
                SELECT count(c.id) FROM company c
                LEFT JOIN campaign_row cr
                ON (c.id = cr.company_id)
                WHERE 1=1
                """);
        createWhere(filter, countQuery);
        return jdbcTemplate.queryForObject(countQuery.toString(), Map.of(), Integer.class);
    }

    private void createWhere(GetCompanyRequestFilter filter, StringBuilder query) {
        filterByCNAE(filter.getCnae(), query);

        if (filter.getNeverSend() != null && filter.getNeverSend()) {
            query.append(" AND cr.company_id IS NULL ");
        }

        query.append(" AND (");
        query.append(" c.email <> '' ");
        if (filter.validMotherBranchId()) {
            query
                    .append(" AND c.mother_branch_identifier = '")
                    .append(filter.getMotherBranchId())
                    .append("'");
        }
        if (filter.getEmailExclude() != null) {
            for (String token : filter.getEmailExclude()) {
                query.append(" AND c.email NOT ILIKE '%").append(token).append("%' ");
            }
        }
        query.append(") ");

        if (filter.getEmailInclude() != null && !filter.getEmailInclude().isEmpty()) {
            query.append(" AND (");
            boolean first = true;
            for (String token : filter.getEmailInclude()) {
                if (!first)
                    query.append("OR ");
                query.append(" c.email ILIKE '%").append(token).append("%' ");
                first = false;
            }
            query.append(") ");
        }

        if (filter.getFantasyNameExclude() != null && !filter.getFantasyNameExclude().isEmpty()) {
            query.append(" AND (");
            query.append(" c.fantasy_name <> '' ");
            for (String name : filter.getFantasyNameExclude()) {
                query.append(" AND c.fantasy_name NOT ILIKE '%").append(name).append("%' ");
            }
            query.append(") ");
        }

        if (filter.getFantasyNameInclude() != null && !filter.getFantasyNameInclude().isEmpty()) {
            query.append(" AND (");
            boolean first = true;
            for (String token : filter.getFantasyNameInclude()) {
                if (!first)
                    query.append("OR ");
                query.append(" c.fantasy_name ILIKE '%").append(token).append("%' ");
                first = false;
            }
            query.append(") ");
        }
    }

    private void filterByCNAE(List<CnaeRequestFilter> cnae, StringBuilder query) {
        if (cnae != null && !cnae.isEmpty()) {
            query.append(" AND c.cnaetaxprimary IN (");
            boolean first = true;
            for (var code : cnae) {
                if (!first) {
                    query.append(",");
                }
                query.append("'").append(code.code()).append("'");
                first = false;
            }
            query.append(")");
        }
    }

    public void deleteByEmail(String email) {
        jdbcTemplate.update("DELETE FROM company WHERE email = :email", Map.of("email", email));
    }

}
