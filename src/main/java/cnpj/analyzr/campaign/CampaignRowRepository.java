package cnpj.analyzr.campaign;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import cnpj.analyzr.email.EmailService.EmailResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CampaignRowRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Optional<CampaignRow> find(Long id) {
        try {
            String sql = "SELECT * FROM campaign_row WHERE id = :id";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, Map.of("id", id), (rs, nr) -> {
                Date sendDate = rs.getDate("send_date");
                Date clickedDate = rs.getDate("clicked_date");
                return CampaignRow.builder()
                        .id(rs.getLong("id"))
                        .leadId(rs.getLong("lead_id"))
                        .success(rs.getBoolean("success"))
                        .errorMsg(rs.getString("error_msg"))
                        .sendDate(sendDate != null ? sendDate.toLocalDate() : null)
                        .opened(rs.getBoolean("opened"))
                        .clicked(rs.getBoolean("clicked"))
                        .clickedDate(clickedDate != null ? clickedDate.toLocalDate() : null)
                        .build();
            }));
        } catch (Exception e) {
            log.error("find id:{} exception: ", id, e);
            return Optional.empty();
        }
    }

    public void updateClick(Long id) {
        try {
            String sql = "UPDATE campaign_row SET clicked = TRUE, clicked_date = CURRENT_DATE  WHERE id = :id";
            jdbcTemplate.update(sql, Map.of("id", id));
        } catch (Exception e) {
            log.error("updateOpen - id:{} exception: ", id, e);
        }
    }

    public void updateOpen(Long id) {
        try {
            String sql = "UPDATE campaign_row SET opened = TRUE WHERE id = :id";
            jdbcTemplate.update(sql, Map.of("id", id));
        } catch (Exception e) {
            log.error("updateOpen - id:{} exception: ", id, e);
        }
    }

    public long insertRow(Long parentId, Long leadId) {
        String sql = """
                INSERT INTO campaign_row(campaign_id, lead_id)
                VALUES (:campaign_id, :lead_id)
                """;

        SqlParameterSource value = new MapSqlParameterSource()
                .addValue("campaign_id", parentId)
                .addValue("lead_id", leadId);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, value, keyHolder);
        return (Long) keyHolder.getKeys().get("id");
    }

    public void updateEmailResponse(Long id, EmailResult result) {
        try {
            String sql = "UPDATE campaign_row SET success = :success, error_msg = :errorMsg, external_id = :externalId WHERE id = :id";
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("success", result.success());
            map.put("errorMsg", result.message());
            map.put("externalId", result.externalId());
            jdbcTemplate.update(sql, map);
        } catch (Exception e) {
            log.error("updateOpen - id:{} exception: ", id, e);
        }
    }

}
