package com.example.ECommerce.Platform.Controller;

import com.example.ECommerce.Platform.DTO.CategoryDTO.CategoryRequest;
import com.example.ECommerce.Platform.DTO.CategoryDTO.CategoryResponse;
import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.Service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @PostMapping("/add/categories")
    public ResponseEntity<CategoryResponse> addCategory(@Valid @RequestBody CategoryRequest categoryRequest){
        return ResponseEntity.ok(categoryService.addCategory(categoryRequest));
    }
    @GetMapping("/get/categories")
    public ResponseEntity<List<CategoryResponse>>getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PutMapping("/update/categories")
    public ResponseEntity<CategoryResponse> updateCategory(@RequestParam String newCategoryId,@RequestParam String subCategoryId){
        return ResponseEntity.ok(categoryService.updateCategory(newCategoryId,subCategoryId));
    }

    @PutMapping("/update/category/name/{categoryId}")
    public ResponseEntity<CategoryResponse>updateCategoryName(@RequestParam String name,@PathVariable String categoryId){
        return ResponseEntity.ok(categoryService.updateCategoryName(name,categoryId));
    }

    @GetMapping("/getbyId/category/{categoryId}")
    public ResponseEntity<CategoryResponse>categoryById(@PathVariable String categoryId){
        return ResponseEntity.ok(categoryService.categoryById(categoryId));
    }

    @DeleteMapping("/delete/category/{categoryId}")
    public ResponseEntity<MessageDTO>deleteCategory(@PathVariable String categoryId) {
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }

    @PutMapping("/reactive/category/{categoryId}")
    public ResponseEntity<MessageDTO>reactiveCategory(@PathVariable String categoryId){
        return ResponseEntity.ok(categoryService.reactiveCategory(categoryId));
    }
}
