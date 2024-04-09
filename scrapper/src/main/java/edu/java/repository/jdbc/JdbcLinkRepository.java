package edu.java.repository.jdbc;

import edu.java.controller.exception.LinkNotFoundException;
import edu.java.domain.LinkEntity;
import edu.java.repository.LinkRepository;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import static edu.java.repository.jdbc.JdbcMapper.listMapToLinkList;

@Repository
@AllArgsConstructor
public class JdbcLinkRepository implements LinkRepository {
    private final static int LINK_TYPE_ADD_PARAMETER_INDEX = 1;
    private final static int URI_ADD_PARAMETER_INDEX = 2;
    private final static int UPDATED_AT_ADD_PARAMETER_INDEX = 3;
    private final static int CHECKED_AT_ADD_PARAMETER_INDEX = 4;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public long add(LinkEntity link) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "insert into links (uri, link_type, updated_at, checked_at) values (?, ?, ?, ?) on conflict(uri)"
                    + " do update set updated_at = EXCLUDED.updated_at, checked_at = EXCLUDED.checked_at returning id",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setString(LINK_TYPE_ADD_PARAMETER_INDEX, link.getUri().toString());
            ps.setString(URI_ADD_PARAMETER_INDEX, link.getLinkType().toString());
            ps.setTimestamp(UPDATED_AT_ADD_PARAMETER_INDEX, Timestamp.from(link.getUpdatedAt().toInstant()));
            ps.setTimestamp(CHECKED_AT_ADD_PARAMETER_INDEX, Timestamp.from(link.getCheckedAt().toInstant()));
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    @Transactional
    public long remove(URI uri) throws LinkNotFoundException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int update = jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                connection.prepareStatement(
                    "delete from links where uri = ? returning id",
                    Statement.RETURN_GENERATED_KEYS
                );
            ps.setString(1, uri.toString());
            return ps;
        }, keyHolder);
        if (update == 0 || keyHolder.getKey() == null) {
            throw new LinkNotFoundException();
        }
        return keyHolder.getKey().longValue();
    }

    @Override
    @Transactional
    public List<LinkEntity> findAll() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from links");
        return listMapToLinkList(list);

    }

    @Override
    @Transactional
    public Optional<LinkEntity> findByUri(URI uri) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                "select * from links where uri = ?",
                new BeanPropertyRowMapper<>(LinkEntity.class),
                uri.toString()
            ));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<LinkEntity> findById(long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                "select * from links where id = ?",
                new BeanPropertyRowMapper<>(LinkEntity.class),
                id
            ));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public List<LinkEntity> findStaleLinks(Long limit) {
        List<Map<String, Object>> list =
            jdbcTemplate.queryForList("select * from links order by checked_at asc limit ?", limit);
        return listMapToLinkList(list).stream().toList();
    }

    @Override
    @Transactional
    public long update(Long linkId, OffsetDateTime updatedAt, OffsetDateTime checkedAt) {
        return jdbcTemplate.update(
            "update links set updated_at = ?, checked_at = ? where id = ?",
            updatedAt,
            checkedAt,
            linkId
        );
    }

}
