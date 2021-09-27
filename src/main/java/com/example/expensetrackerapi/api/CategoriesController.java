package com.example.expensetrackerapi.api;

import com.example.expensetrackerapi.domain.Category;
import com.example.expensetrackerapi.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
public class CategoriesController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("categories")
    public ResponseEntity<List<Category>> getAllCategories(HttpServletRequest request) {
        int userId = (Integer) request.getAttribute("userId");
        List<Category> categories = categoryService.fetchAllCategories(userId);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("categories/{categoryId}")
    public ResponseEntity<Category> getCategoryById(
            HttpServletRequest request,
            @PathVariable("categoryId") Integer categoryId
    ) {
        int userId = (Integer) request.getAttribute("userId");

        Category category = categoryService.fetchCategoryById(userId, categoryId);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PostMapping("categories")
    public ResponseEntity<Category> addCategory(
            HttpServletRequest request,
            @RequestBody Map<String, Object> categoriesMap
    ) {
        int userId = (Integer) request.getAttribute("userId");

        String title = (String) categoriesMap.get("title");
        String description = (String) categoriesMap.get("description");

        Category category = categoryService.addCategory(userId, title, description);

        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @PutMapping("categories/{categoryId}")
    public ResponseEntity<Map<String, Boolean>> updateCategory(
            HttpServletRequest request,
            @PathVariable("categoryId") Integer categoryId,
            @RequestBody Category category
    ) {
        int userId = (Integer) request.getAttribute("userId");

        categoryService.updateCategory(userId, categoryId, category);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("categories/{categoryId}")
    public ResponseEntity<Map<String, Boolean>> deleteCategory(
        HttpServletRequest request,
        @PathVariable("categoryId") Integer categoryId
    ) {
        int userId = (Integer) request.getAttribute("userId");

        categoryService.removeCategoryWithAllTransactions(userId, categoryId);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Success", true);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

}
