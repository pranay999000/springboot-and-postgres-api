package com.example.expensetrackerapi.repositories;

import com.example.expensetrackerapi.domain.Transaction;
import com.example.expensetrackerapi.exception.EtBadRequestException;
import com.example.expensetrackerapi.exception.EtResourceNotFound;

import java.util.List;

public interface TransactionRepository {

    List<Transaction> findAll(Integer userId, Integer categoryId);

    Transaction findById(Integer userId, Integer categoryId, Integer transactionId) throws EtResourceNotFound;

    Integer create(Integer userId, Integer categoryId, Double amount, String note, Long transactionDate) throws EtBadRequestException;

    void update(Integer userId, Integer categoryId, Integer transactionId, Transaction transaction) throws EtBadRequestException;

    void remove(Integer userId, Integer categoryId, Integer transactionId) throws EtResourceNotFound;

}
