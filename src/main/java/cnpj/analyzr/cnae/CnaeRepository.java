package cnpj.analyzr.cnae;

import java.util.List;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import cnpj.analyzr.cnae.CnaeController.Cnae;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CnaeRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Cnae> find() {
        String sql = """
                SELECT *
                FROM cnae
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return Cnae.builder().code(rs.getInt("code")).description(rs.getString("description")).build();
        });
    }
}
