package com.example.expensetrackerapi.repositories;

import com.example.expensetrackerapi.domain.Category;
import com.example.expensetrackerapi.exception.EtBadRequestException;
import com.example.expensetrackerapi.exception.EtResourceNotFound;

import java.util.List;

public interface CategoryRepository {

    List<Category> findAll(Integer userId) throws EtResourceNotFound;

    Category findById(Integer userId, Integer categoryId) throws EtResourceNotFound;

    Integer create(Integer userId, String title, String description) throws EtBadRequestException;

    void update(Integer userId, Integer categoryId, Category category) throws EtBadRequestException;

    void removeById(Integer userId, Integer categoryId);

}
