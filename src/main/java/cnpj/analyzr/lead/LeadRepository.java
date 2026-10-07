
package cnpj.analyzr.lead;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import cnpj.analyzr.lead.LeadController.LeadTotalDashboard;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@AllArgsConstructor
@Slf4j
public class LeadRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void insert(List<String> emails) {
        String sql = "INSERT INTO lead(email) VALUES(?) ON CONFLICT(email) DO NOTHING";

        jdbcTemplate.getJdbcTemplate().batchUpdate(sql, emails, emails.size(), (ps, arg) -> {
            ps.setString(1, arg);
        });
    }

    public void unsubscribe(Long id) {
        String sql = "UPDATE lead SET unsubscribed = true WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    public List<LeadRecord> find(List<Long> ids) {
        String sql = """
                SELECT * FROM lead WHERE id IN (:ids)
                """;

        return executeQueryList(sql, Map.of("ids", ids));
    }

    public void updateOpen(Long id, int open) {
        try {
            jdbcTemplate.update("UPDATE lead SET open = :open WHERE id = :id",
                    Map.of("id", id, "open", open));
        } catch (Exception e) {
            log.error("updateOpen - id:{} open:{} exception: ", id, open, e);
        }
    }

    public void updateClick(Long id, int click) {
        try {
            jdbcTemplate.update("UPDATE lead SET click = :click, last_click_date = CURRENT_DATE WHERE id = :id",
                    Map.of("id", id, "click", click));
        } catch (Exception e) {
            log.error("updateOpen - id:{} click:{} exception: ", id, click, e);
        }
    }

    public void updateUnrechable(Long id) {
        try {
            jdbcTemplate.update("UPDATE lead SET unreachable = true WHERE id = :id", Map.of("id", id));
        } catch (Exception e) {
            log.error("updateUnrechable - id:{} exception: ", id, e);
        }
    }

    public Optional<LeadRecord> find(Long id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("SELECT * FROM lead WHERE id = :id", Map.of("id", id), (rs, rn) -> {
                        Date lastSend = rs.getDate("last_send");
                        Date lastClick = rs.getDate("last_click_date");
                        return LeadRecord.builder()
                                .id(rs.getLong("id"))
                                .email(rs.getString("email"))
                                .lastSend(lastSend != null ? lastSend.toLocalDate() : null)
                                .open(rs.getInt("open"))
                                .click(rs.getInt("click"))
                                .lastClickDate(lastClick != null ? lastClick.toLocalDate() : null)
                                .build();
                    }));
        } catch (Exception e) {
            log.info("find id:{} exception:", id, e);
        }
        return Optional.empty();
    }

    public Optional<LeadRecord> find(String email) {
        try {
            String sql = "SELECT * FROM lead WHERE email = :email";
            var obj = jdbcTemplate.queryForObject(sql, Map.of("email", email), (rs, rn) -> buildFromResultSet(rs));
            return Optional.ofNullable(obj);
        } catch (CannotGetJdbcConnectionException e) {
            log.error("find by email:{} exception:", email, e);
            throw e;
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            log.error("find by email:{} exception: ", email, e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Long> findId(String email) {
        try {
            String sql = "SELECT id FROM lead WHERE email = :email";
            Long id = jdbcTemplate.queryForObject(sql, Map.of("email", email), Long.class);
            return Optional.ofNullable(id);
        } catch (CannotGetJdbcConnectionException e) {
            log.error("find by email:{} exception:", email, e);
            throw e;
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            log.error("find by email:{} exception: ", email, e.getMessage());
            return Optional.empty();
        }
    }

    public void updatePlusCounter(Long id, Integer sendQuantity) {
        String sql = """
                UPDATE lead SET send_quantity = :send_quantity, last_send = now() WHERE id = :id
                """;

        jdbcTemplate.update(sql, Map.of("send_quantity", sendQuantity, "id", id));
    }

    public void updateCounter(Long id, Integer sendQuantity) {
        String sql = """
                UPDATE lead SET send_quantity = :send_quantity WHERE id = :id
                """;

        jdbcTemplate.update(sql, Map.of("send_quantity", sendQuantity, "id", id));
    }

    public int countNoFilter() {
        String sql = "SELECT COUNT(*) FROM lead";
        return jdbcTemplate.queryForObject(sql.toString(), Map.of(), Integer.class);
    }

    public LeadTotalDashboard findTotalDashboard() {
        String sql = """
                SELECT
                    COUNT(id) AS total,
                    SUM(open) AS open,
                    SUM(click) AS click,
                    COUNT(*) FILTER (WHERE unsubscribed IS TRUE) as unsubscribed,
                    COUNT(*) FILTER (WHERE unreachable IS TRUE) as unreachable
                FROM lead
                WHERE send_quantity > 0;
                """;
        return jdbcTemplate.queryForObject(sql, Map.of(), (rs, rn) -> {
            return new LeadTotalDashboard(null, rs.getInt("total"), rs.getInt("open"), rs.getInt("click"),
                    rs.getInt("unsubscribed"), rs.getInt("unreachable"));
        });
    }

    public int count(LeadFilterRecord filter) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(DISTINCT l.id) from lead l
                WHERE 1=1
                """);

        Map<String, Object> map = buildQuery(filter, sql);

        return jdbcTemplate.queryForObject(sql.toString(), map, Integer.class);
    }

    public List<LeadRecord> find(LeadFilterRecord filter) {
        StringBuilder sql = new StringBuilder("""
                SELECT l.* from lead l
                WHERE 1=1
                """);

        // it can add a WHERE clause before or
        // after the `buildQuery` method
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
            return buildFromResultSet(rs);
        });
    }

    private LeadRecord buildFromResultSet(ResultSet rs) throws SQLException {
        return LeadRecord.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .send(rs.getInt("send_quantity"))
                .build();
    }

    private Map<String, Object> buildQuery(LeadFilterRecord filter, StringBuilder query) {
        Map<String, Object> map = new HashMap<>();

        query.append(" AND l.id > ").append(filter.lastIndex()).append(" ");
        query.append(" AND (l.unreachable IS FALSE OR l.unreachable IS NULL) ");

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

        if (filter.shouldFetchUnsubscribed() == null || !filter.shouldFetchUnsubscribed()) {
            query.append(" AND l.unsubscribed IS FALSE");
        }

        if (filter.sentQuantity() != null && filter.sentQuantity() >= 0) {
            query.append(" AND l.send_quantity = :quantitySent ");
            map.put("quantitySent", filter.sentQuantity());
        }

        return map;
    }
}
