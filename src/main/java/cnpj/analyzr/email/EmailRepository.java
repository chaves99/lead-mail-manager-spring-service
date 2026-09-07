package cnpj.analyzr.email;


import java.util.List;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@AllArgsConstructor
@Slf4j
public class EmailRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<String> find() {
        StringBuilder sql = new StringBuilder("");
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            return rs.getString("");
        });
    }
}
