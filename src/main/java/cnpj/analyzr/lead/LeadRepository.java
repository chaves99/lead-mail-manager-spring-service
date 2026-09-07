
package cnpj.analyzr.lead;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@AllArgsConstructor
@Slf4j
public class LeadRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<LeadRecord> find(List<Long> ids) {
        String sql = """
                SELECT * FROM lead WHERE id IN (:ids)
                """;

        return executeQueryList(sql, Map.of("ids", ids));
    }

    public void update(Long id, Integer sendQuantity) {
        String sql = """
            UPDATE lead SET send_quantity = :send_quantity, last_send = now() WHERE id = :id
            """;

        jdbcTemplate.update(sql, Map.of("send_quantity", sendQuantity, "id", id));
    }


    public int count(LeadFilterRecord filter) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(DISTINCT l.id) from lead l
                LEFT JOIN establishment es ON (l.id = es.lead_id)
                WHERE 1=1
                """);

        Map<String, Object> map = buildQuery(filter, sql);

        return jdbcTemplate.queryForObject(sql.toString(), map, Integer.class);
    }

    public List<LeadRecord> find(LeadFilterRecord filter) {
        StringBuilder sql = new StringBuilder("""
                SELECT l.* from lead l
                LEFT JOIN establishment es ON (l.id = es.lead_id)
                WHERE 1=1
                """);

        Map<String, Object> map = buildQuery(filter, sql);

        sql.append(" GROUP BY l.id ORDER BY l.id ");

        if (filter.pageSize() != null && filter.pageSize() > 0) {
            sql.append(" LIMIT :limit ");
            map.put("limit", filter.pageSize());
        }

        return executeQueryList(sql.toString(), map);
    }

    private List<LeadRecord> executeQueryList(String sql, Map<String, Object> map) {
        return jdbcTemplate.query(sql, map, (rs, rowNum) -> {
            return new LeadRecord(rs.getLong("id"), rs.getString("email"), rs.getInt("send_quantity"));
        });
    }

    private Map<String, Object> buildQuery(LeadFilterRecord filter, StringBuilder query) {
        Map<String, Object> map = new HashMap<>();

        if (filter.emailExclude() != null && !filter.emailExclude().isEmpty()) {
            boolean first = true;
            query.append(" AND (");
            for (String token : filter.emailExclude()) {
                if (!first)
                    query.append(" AND ");
                query.append(" l.email NOT ILIKE '%").append(token).append("%' ");
                first = false;
            }
            query.append(") ");
        }

        if (filter.emailInclude() != null && !filter.emailInclude().isEmpty()) {
            query.append(" AND (");
            boolean first = true;
            for (String token : filter.emailInclude()) {
                if (!first)
                    query.append("OR ");
                query.append(" l.email ILIKE '%").append(token).append("%' ");
                first = false;
            }
            query.append(") ");
        }

        if (filter.sentQuantity() != null && filter.sentQuantity() >= 0) {
            query.append(" AND l.send_quantity = :quantitySent ");
            map.put("quantitySent", filter.sentQuantity());
        }

        if (filter.cnae() != null && !filter.cnae().isEmpty()) {
            int counter = 0;
            String keyPrefix = "cnae_";
            for (var cnae : filter.cnae()) {
                String key = keyPrefix + counter;
                query.append(" AND es.cnae_tax_primary = :" + key + " ");
                counter++;
                map.put(key, cnae.code().toString());
            }
        }

        if (filter.fantasyNameExclude() != null && !filter.fantasyNameExclude().isEmpty()) {
            query.append(" AND (");
            query.append(" es.fantasy_name <> '' ");
            for (String name : filter.fantasyNameExclude()) {
                query.append(" AND es.fantasy_name NOT ILIKE '%").append(name).append("%' ");
            }
            query.append(") ");
        }

        if (filter.fantasyNameInclude() != null && !filter.fantasyNameInclude().isEmpty()) {
            query.append(" AND (");
            boolean first = true;
            for (String token : filter.fantasyNameInclude()) {
                if (!first)
                    query.append("OR ");
                query.append(" es.fantasy_name ILIKE '%").append(token).append("%' ");
                first = false;
            }
            query.append(") ");
        }

        if (filter.validMotherBranchId()) {
            query.append(" AND es.mother_branch_identifier = :mother_branch_identifier");
            map.put("mother_branch_identifier", filter.motherBranchId().toString());
        }

        return map;
    }
}
