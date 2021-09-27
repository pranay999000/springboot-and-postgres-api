package com.example.expensetrackerapi.repositories;

import com.example.expensetrackerapi.domain.User;
import com.example.expensetrackerapi.exception.EtAuthException;

public interface UserRepository {

    Integer create(String first_name, String last_name, String email, String password) throws EtAuthException;

    User findByEmailAndPassword(String email, String password) throws EtAuthException;

    Integer getCountByEmail(String email);

    User findById(Integer user_id);

}
