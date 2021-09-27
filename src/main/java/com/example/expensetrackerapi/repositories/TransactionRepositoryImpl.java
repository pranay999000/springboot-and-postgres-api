package com.example.expensetrackerapi.repositories;

import com.example.expensetrackerapi.domain.Transaction;
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
public class TransactionRepositoryImpl implements TransactionRepository{

    private static final String SQL_CREATE =
            "INSERT INTO et_transactions (transaction_id, categories_id, user_id, amount, note, transaction_date)" +
                    " VALUES(NEXTVAL('et_transactions_seq'), ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID =
            "SELECT transaction_id, categories_id, user_id, amount, note, transaction_date" +
                    " FROM et_transactions WHERE user_id = ? AND transaction_id = ? AND categories_id = ?";

    private static final String SQL_FIND_ALL =
            "SELECT transaction_id, categories_id, user_id, amount, note, transaction_date" +
                    " FROM et_transactions WHERE user_id = ? AND categories_id = ?";

    private static final String SQL_UPDATE =
            "UPDATE et_transactions SET amount = ?, note = ?, transaction_date = ?" +
                    "WHERE user_id = ? AND categories_id = ? AND transaction_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM et_transactions WHERE user_id = ? AND categories_id = ? AND transaction_id = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Transaction> findAll(Integer userId, Integer categoryId) {
        return jdbcTemplate.query(SQL_FIND_ALL, new Object[]{userId, categoryId}, transactionRowMapper);
    }

    @Override
    public Transaction findById(Integer userId, Integer categoryId, Integer transactionId) throws EtResourceNotFound {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new Object[]{userId, transactionId, categoryId}, transactionRowMapper);
        } catch (Exception e) {
            throw new EtResourceNotFound("Not found - " + e);
        }
    }

    @Override
    public Integer create(Integer userId, Integer categoryId, Double amount, String note, Long transactionDate) throws EtBadRequestException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, categoryId);
                preparedStatement.setInt(2, userId);
                preparedStatement.setDouble(3, amount);
                preparedStatement.setString(4, note);
                preparedStatement.setLong(5, transactionDate);

                return preparedStatement;
            }, keyHolder);

            return (Integer) Objects.requireNonNull(keyHolder.getKeys()).get("transaction_id");
        } catch (Exception e) {
            throw new EtBadRequestException("Bad request - " + e.getMessage());
        }
    }

    @Override
    public void update(Integer userId, Integer categoryId, Integer transactionId, Transaction transaction) throws EtBadRequestException {
        try {
            jdbcTemplate.update(SQL_UPDATE, new Object[]{transaction.getAmount(), transaction.getNote(), transaction.getTransactionDate(), userId, categoryId, transactionId});
        } catch (Exception e) {
            throw new EtBadRequestException("Bad request - " + e.getMessage());
        }
    }

    @Override
    public void remove(Integer userId, Integer categoryId, Integer transactionId) throws EtResourceNotFound {
        int count = jdbcTemplate.update(SQL_DELETE, new Object[]{userId, categoryId, transactionId});

        if (count == 0)
            throw new EtResourceNotFound("Not Found");
    }

    private final RowMapper<Transaction> transactionRowMapper = (((rs, rowNum) -> new Transaction(
            rs.getInt("transaction_id"),
            rs.getInt("categories_id"),
            rs.getInt("user_id"),
            rs.getDouble("amount"),
            rs.getString("note"),
            rs.getLong("transaction_date")
    )));
}
