package com.example.expensetrackerapi.service;

import com.example.expensetrackerapi.domain.User;
import com.example.expensetrackerapi.exception.EtAuthException;
import com.example.expensetrackerapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Override
    public User validateUser(String email, String password) throws EtAuthException {

        if (email != null) email = email.toLowerCase();
        return userRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public User registerUser(String first_name, String last_name, String email, String password) throws EtAuthException {

        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        if (email != null) email = email.toLowerCase();

        if (!pattern.matcher(email).matches()) throw new EtAuthException("Invalid Email format");

        Integer count = userRepository.getCountByEmail(email);
        if (count > 0) throw new EtAuthException("Email already in use");

        Integer userId = userRepository.create(first_name, last_name, email, password);

        return userRepository.findById(userId);

    }
}
