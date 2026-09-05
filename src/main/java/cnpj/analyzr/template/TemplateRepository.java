package cnpj.analyzr.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TemplateRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Optional<Template> find(Long id) {
        try {
            String sql = """
                    SELECT * FROM template WHERE id = :id
                    """;
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);

            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, map, (rs, rowNum) -> {
                return new Template(rs.getLong("id"), rs.getString("subject"), rs.getString("body"),
                        rs.getString("name"));
            }));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    public void insert(Template template) {
        String sql = """
                INSERT INTO template (subject, body, name) VALUES(:subject, :body, :name)
                """;
        Map<String, Object> map = new HashMap<>();
        map.put("subject", template.subject());
        map.put("body", template.body());
        map.put("name", template.name());

        jdbcTemplate.update(sql, map);
    }

    public void update(Template template) {
        String sql = """
                UPDATE template
                SET subject = :subject, body = :body, name = :name
                WHERE id = :id
                """;

        Map<String, Object> map = new HashMap<>();
        map.put("subject", template.subject());
        map.put("body", template.body());
        map.put("name", template.name());
        map.put("id", template.id());

        jdbcTemplate.update(sql, map);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM template WHERE id = :id";

        Map<String, Object> map = new HashMap<>();
        map.put("id", id);

        jdbcTemplate.update(sql, map);
    }

    public List<Template> get() {
        String sql = """
                SELECT * FROM template
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Template(rs.getLong("id"), rs.getString("subject"), rs.getString("body"), rs.getString("name"));
        });
    }
}
