package test;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class JdbcDAO implements DataDAO {

    private JdbcTemplate jdbcTemplate;

    public JdbcDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Object> execute(String query, List<Object> parameters) {
        Map<String, Object> xx = this.jdbcTemplate.query(query, parameters.toArray(), extractor -> {
            if (extractor.next()) {
                return new ColumnMapRowMapper().mapRow(extractor, 0);
            } else {
                return null;
            }

        });
        return xx;
    }
}
