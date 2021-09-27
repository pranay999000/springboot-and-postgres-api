package com.example.expensetrackerapi.repositories;

import com.example.expensetrackerapi.Constants;
import com.example.expensetrackerapi.domain.User;
import com.example.expensetrackerapi.exception.EtAuthException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.awt.image.BandCombineOp;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final String SQL_CREATE =
            "INSERT INTO et_users(user_id, first_name, last_name, email, password) VALUES(NEXTVAL('et_users_seq'), ?, ?, ?, ?)";

    private static final String SQL_COUNT_BY_EMAIL =
            "SELECT COUNT(*) FROM et_users WHERE email = ?";

    private static final String SQL_FIND_BY_USER_ID =
            "SELECT user_id, first_name, last_name, email, password FROM et_users WHERE user_id = ?";


    private static final String SQL_FIND_BY_EMAIL =
            "SELECT user_id, first_name, last_name, email, password FROM et_users WHERE email = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Integer create(String firstName, String lastname, String email, String password) throws EtAuthException {
        try {

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastname);
                preparedStatement.setString(3, email);
                preparedStatement.setString(4, hashedPassword);

                return preparedStatement;
            }, keyHolder);

            return (Integer) Objects.requireNonNull(keyHolder.getKeys()).get("user_id");

        } catch (Exception e) {
            throw new EtAuthException("Invalid details, Failed to create account " + e.getMessage());
        }
    }

    @Override
    public User findByEmailAndPassword(String email, String password) throws EtAuthException {

        try {
            User user = jdbcTemplate.queryForObject(SQL_FIND_BY_EMAIL, new Object[]{email}, userRowMapper);

            if (!BCrypt.checkpw(password, user.getPassword()))
                throw new EtAuthException("Invalid password");

            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new EtAuthException("Invalid credentials " + e.getMessage());
        }

    }

    @Override
    public Integer getCountByEmail(String email) {
        return jdbcTemplate.queryForObject(SQL_COUNT_BY_EMAIL, new Object[]{email}, Integer.class);
    }

    @Override
    public User findById(Integer userId) {
        return jdbcTemplate.queryForObject(SQL_FIND_BY_USER_ID, new Object[]{userId}, userRowMapper);
    }

    private final RowMapper<User> userRowMapper = ((rs, rowNum) -> new User(
            rs.getInt("user_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("password")
    ));

}
