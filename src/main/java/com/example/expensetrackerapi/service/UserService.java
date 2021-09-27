package com.example.expensetrackerapi.service;

import com.example.expensetrackerapi.domain.User;
import com.example.expensetrackerapi.exception.EtAuthException;

public interface UserService {

    User validateUser(String email, String password) throws EtAuthException;

    User registerUser(String first_name, String last_name, String email, String password) throws EtAuthException;

}
