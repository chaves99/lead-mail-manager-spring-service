package cnpj.analyzr.campaign;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import cnpj.analyzr.campaign.entity.Campaign;
import cnpj.analyzr.campaign.entity.res.CampaignRecordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CampaignRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<CampaignRecordResponse> find() {
        String sql = """
                SELECT c.*,
                    COUNT(*) FILTER (WHERE cr.success IS TRUE) as success,
                    COUNT(*) FILTER (WHERE cr.success IS NOT TRUE AND cr.success IS NOT NULL) as failure,
                    te.id as te_id, te.name as te_name
                FROM campaign c
                LEFT JOIN campaign_row cr
                ON (c.id = cr.campaign_id)
                LEFT JOIN template te ON (c.template_id = te.id)
                GROUP BY c.id, te.id
                ORDER BY c.id
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return CampaignRecordResponse.builder()
                    .id(rs.getLong("id"))
                    .description(rs.getString("description"))
                    .templateId(rs.getLong("te_id"))
                    .templateName(rs.getString("te_name"))
                    .rowCount(rs.getInt("company_count"))
                    .success(rs.getInt("success"))
                    .failure(rs.getInt("failure"))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .build();
        });
    }

    public Optional<Campaign> find(Long id) {
        try {
            String sql = "SELECT * from campaign WHERE id = :id";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, Map.of("id", id), (rs, rn) -> {
                return Campaign.builder()
                        .id(rs.getLong("id"))
                        .description(rs.getString("description"))
                        .templateId(rs.getLong("template_id"))
                        .rowCount(rs.getInt("company_count"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .build();
            }));
        } catch (Exception e) {
            log.error("find by id:{} exception: ", id, e);
        }

        return Optional.empty();
    }

    public Long insert(String description, Long templateId, int company_count) {
        String sql = """
                INSERT INTO campaign(description, template_id, company_count, created_at)
                VALUES (:description, :template_id, :company_count, now())
                """;
        MapSqlParameterSource value = new MapSqlParameterSource()
                .addValue("description", description)
                .addValue("company_count", company_count)
                .addValue("template_id", templateId);

        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, value, holder);
        return (Long) holder.getKeys().get("id");
    }

    public void update(Long id, String description) {
        String sql = "UPDATE campaign SET description = :description WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id, "description", description));
    }

}
