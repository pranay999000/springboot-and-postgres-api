package com.example.expensetrackerapi.service;

import com.example.expensetrackerapi.domain.Category;
import com.example.expensetrackerapi.exception.EtBadRequestException;
import com.example.expensetrackerapi.exception.EtResourceNotFound;

import java.util.List;

public interface CategoryService {

    List<Category> fetchAllCategories(Integer userId);

    Category fetchCategoryById(Integer userId, Integer categoryId) throws EtResourceNotFound;

    Category addCategory(Integer userId, String title, String description) throws EtBadRequestException;

    void updateCategory(Integer userId, Integer categoryId, Category category) throws EtBadRequestException;

    void removeCategoryWithAllTransactions(Integer userId, Integer categoryId) throws EtResourceNotFound;

}
