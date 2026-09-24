package cnpj.analyzr.campaign.row;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import cnpj.analyzr.email.EmailServiceInterface.EmailResult;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CampaignRowRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Optional<CampaignRow> findById(Long id) {
        try {
            String sql = "SELECT * FROM campaign_row WHERE id = :id";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, Map.of("id", id), (rs, nr) -> {
                return buildEntity(rs);
            }));
        } catch (Exception e) {
            log.error("find id:{} exception: ", id, e);
            return Optional.empty();
        }
    }

    private CampaignRow buildEntity(ResultSet rs) throws SQLException {
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
    }

    public List<CampaignRow> findAll(Long campaignId) {
        String sql = "SELECT * FROM campaign_row WHERE campaign_id = :campaign_id";
        return jdbcTemplate.query(sql, Map.of("campaign_id", campaignId), (rs, rn) -> {
            return buildEntity(rs);
        });
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
            String sql = "UPDATE campaign_row SET success = :success, error_msg = :errorMsg WHERE id = :id";
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("success", result.success());
            map.put("errorMsg", result.message());
            jdbcTemplate.update(sql, map);
        } catch (Exception e) {
            log.error("updateOpen - id:{} exception: ", id, e);
        }
    }

    public CampaignRowTotalsRecord findTotalsByCampaignid(Long campaignId) {
        String sql = """
                SELECT
                    COUNT(*) FILTER (WHERE success IS TRUE) as success,
                    COUNT(*) FILTER (WHERE success IS FALSE) as error,
                    COUNT(*) FILTER (WHERE opened IS TRUE) as opened,
                    COUNT(*) FILTER (WHERE clicked IS TRUE) as clicked
                FROM campaign_row
                WHERE campaign_id = :campaign_id
                GROUP BY campaign_id
                """;

        return jdbcTemplate.queryForObject(sql, Map.of("campaign_id", campaignId), (rs, rn) -> {
            return CampaignRowTotalsRecord.builder()
                    .success(rs.getInt("success"))
                    .error(rs.getInt("error"))
                    .opened(rs.getInt("opened"))
                    .clicked(rs.getInt("clicked"))
                    .build();
        });

    }

    public void deleteAll(Long id) {
        String sql = "DELETE FROM campaign_row WHERE campaign_id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    @Builder
    public static record CampaignRowTotalsRecord(int success, int error, int opened, int clicked) {
    }
}
