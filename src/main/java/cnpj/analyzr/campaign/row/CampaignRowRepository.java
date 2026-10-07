package cnpj.analyzr.campaign.row;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

    public Optional<CampaignRow> findByIdAndLeadId(Long id, Long leadId) {
        try {
            String sql = "SELECT * FROM campaign_row WHERE id = :id AND lead_id = :lead_id";
            Map<String, Long> of = Map.of("id", id, "lead_id", leadId);
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, of, (rs, nr) -> {
                return buildEntity(rs);
            }));
        } catch (Exception e) {
            log.error("findByIdAndLeadId id:{} leadId:{} exception: ", id, leadId, e);
            return Optional.empty();
        }
    }

    private CampaignRow buildEntity(ResultSet rs) throws SQLException {
        Timestamp sendDate = rs.getTimestamp("send_date");
        Date clickedDate = rs.getDate("clicked_date");
        return CampaignRow.builder()
                .id(rs.getLong("id"))
                .leadId(rs.getLong("lead_id"))
                .success(rs.getBoolean("success"))
                .errorMsg(rs.getString("error_msg"))
                .sendDate(sendDate != null ? sendDate.toLocalDateTime() : null)
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

    public void updateEmailStatus(Long id, CampaignRow.Status status) {
        try {
            String sql = "UPDATE campaign_row SET status = :status WHERE id = :id";
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("status", status.getCode());
            jdbcTemplate.update(sql, map);
        } catch (Exception e) {
            log.error("updateOpen - id:{} exception: ", id, e);
        }
    }

    public CampaignRowTotalsRecord findTotalsByCampaignid(Long campaignId) {
        String sql = """
                SELECT
                    COUNT(*) FILTER (WHERE opened IS TRUE) as opened,
                    COUNT(*) FILTER (WHERE clicked IS TRUE) as clicked,

                    COUNT(*) FILTER (WHERE status = 1) as pending,
                    COUNT(*) FILTER (WHERE status = 2) as delivered,
                    COUNT(*) FILTER (WHERE status = 3) as bounced,
                    COUNT(*) FILTER (WHERE status = 4) as complained,
                    COUNT(*) FILTER (WHERE status = 5) as unknow_error
                FROM campaign_row
                WHERE campaign_id = :campaign_id
                GROUP BY campaign_id
                """;

        return jdbcTemplate.queryForObject(sql, Map.of("campaign_id", campaignId), (rs, rn) -> {
            return CampaignRowTotalsRecord.builder()
                    .pending(rs.getInt("pending"))
                    .delivered(rs.getInt("delivered"))
                    .bounced(rs.getInt("bounced"))
                    .complained(rs.getInt("complained"))
                    .unknowError(rs.getInt("unknow_error"))
                    .opened(rs.getInt("opened"))
                    .clicked(rs.getInt("clicked"))
                    .build();
        });
    }

    public void deleteByLead(Long leadId) {
        String sql = "DELETE FROM campaign_row WHERE lead_id = :id";
        jdbcTemplate.update(sql, Map.of("id", leadId));
    }

    public void deleteAll(Long campaignId) {
        String sql = "DELETE FROM campaign_row WHERE campaign_id = :id";
        jdbcTemplate.update(sql, Map.of("id", campaignId));
    }

    @Builder
    public static record CampaignRowTotalsRecord(int pending, int delivered,
            int bounced, int complained, int unknowError,
            int opened, int clicked) {
    }
}
