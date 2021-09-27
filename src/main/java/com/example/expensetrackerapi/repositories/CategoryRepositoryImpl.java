package com.example.expensetrackerapi.repositories;

import com.example.expensetrackerapi.domain.Category;
import com.example.expensetrackerapi.exception.EtBadRequestException;
import com.example.expensetrackerapi.exception.EtResourceNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository{

    private static final String SQL_CREATE =
            "INSERT INTO et_categories (categories_id, user_id, title, description) VALUES (NEXTVAL('et_categories_seq'), ?, ?, ?)";

    private static final String SQL_FIND_BY_ID =
            "SELECT C.categories_id, C.user_id, C.title, C.description," +
                    " COALESCE(SUM (T.amount), 0) total_expense" +
                    " FROM et_transactions T RIGHT OUTER JOIN et_categories C ON C.categories_id = T.categories_id" +
                    " WHERE C.user_id = ? AND C.categories_id = ? GROUP BY C.categories_id";

    private static final String SQL_FIND_ALL =
            "SELECT C.categories_id, C.user_id, C.title, C.description," +
                    " COALESCE(SUM (T.amount), 0) total_expense" +
                    " FROM et_transactions T RIGHT OUTER JOIN et_categories C ON C.categories_id = T.categories_id" +
                    " WHERE C.user_id = ? GROUP BY C.categories_id";

    private static final String SQL_UPDATE =
            "UPDATE et_categories SET title = ?, description = ? WHERE user_id = ? AND categories_id = ?";

    private static final String SQL_DELETE_CATEGORY =
            "DELETE FROM et_categories WHERE user_id = ? AND categories_id = ?";

    private static final String SQL_DELETE_TRANSACTIONS =
            "DELETE FROM et_transactions WHERE categories_id = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Category> findAll(Integer userId) throws EtResourceNotFound {
        return jdbcTemplate.query(SQL_FIND_ALL, new Object[]{userId}, categoryRowMapper);
    }

    @Override
    public Category findById(Integer userId, Integer categoryId) throws EtResourceNotFound {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new Object[]{userId, categoryId}, categoryRowMapper);
        } catch (Exception e) {
            throw new EtResourceNotFound("Category not found - " + e.getMessage());
        }
    }

    @Override
    public Integer create(Integer userId, String title, String description) throws EtBadRequestException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, userId);
                preparedStatement.setString(2, title);
                preparedStatement.setString(3, description);
                return preparedStatement;
            }, keyHolder);

            return (Integer) Objects.requireNonNull(keyHolder.getKeys()).get("categories_id");
        } catch (Exception e) {
            throw new EtBadRequestException("Invalid request - " + e.getMessage());
        }
    }

    @Override
    public void update(Integer userId, Integer categoryId, Category category) throws EtBadRequestException {
        try {
            jdbcTemplate.update(SQL_UPDATE, new Object[]{category.getTitle(), category.getDescription(), userId, categoryId});
        } catch (Exception e) {
            throw new EtBadRequestException("Bad request - " + e.getMessage());
        }
    }

    @Override
    public void removeById(Integer userId, Integer categoryId) {
        this.removeAllCatTransactions(categoryId);

        jdbcTemplate.update(SQL_DELETE_CATEGORY, new Object[]{userId, categoryId});
    }

    private void removeAllCatTransactions(Integer categoryId) {
        jdbcTemplate.update(SQL_DELETE_TRANSACTIONS, new Object[]{categoryId});
    }

    private final RowMapper<Category> categoryRowMapper = (((rs, rowNum) -> new Category(rs.getInt("categories_id"),
            rs.getInt("user_id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getDouble("total_expense"))));
}
