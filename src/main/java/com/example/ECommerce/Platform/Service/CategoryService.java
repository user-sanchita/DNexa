package com.example.ECommerce.Platform.Service;

import com.example.ECommerce.Platform.DTO.CategoryDTO.CategoryRequest;
import com.example.ECommerce.Platform.DTO.CategoryDTO.CategoryResponse;
import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.Exception.AlreadyDoneException;
import com.example.ECommerce.Platform.Exception.NotActivatedException;
import com.example.ECommerce.Platform.Exception.SameException;
import com.example.ECommerce.Platform.Exception.NotFoundException;
import com.example.ECommerce.Platform.Model.Category;
import com.example.ECommerce.Platform.Repository.CategoryRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryResponse addCategory(@Valid CategoryRequest categoryRequest) {

        Category parentCategory = null;

        if(categoryRequest.getParentId()!=null){

            parentCategory = categoryRepository.findById(categoryRequest.getParentId()).orElseThrow(
                    () -> new NotFoundException("Parent not found"));
            if(categoryRepository.existsByCategoryNameAndParentCategory(categoryRequest.getCategoryName(), parentCategory)){
                throw new AlreadyDoneException("Sub category already exists");
            }

        }else{
            if(categoryRepository.existsByCategoryNameAndParentCategoryIsNull(categoryRequest.getCategoryName())){
                throw new AlreadyDoneException("Category already exists");
            }
        }

        Category category = Category.builder()
                .categoryName(categoryRequest.getCategoryName())
                .parentCategory(parentCategory)
                .build();

        return mapToResponse(categoryRepository.save(category));
    }


    private @Nullable CategoryResponse mapToResponse(Category savedCategory) {

        CategoryResponse responseAdmin = new CategoryResponse();
        responseAdmin.setCategoryId(savedCategory.getCategoryId());
        responseAdmin.setCategoryName(savedCategory.getCategoryName());

        if (savedCategory.getSubCategories() != null) {
            responseAdmin.setSubCategories(
                    savedCategory.getSubCategories()
                            .stream()
                            .filter(Category::isActive)
                            .map(this::mapToResponse)
                            .toList()
            );
        }
        return responseAdmin;
    }


    public @Nullable List<CategoryResponse> getAllCategories() {

        List<Category> category = categoryRepository.findByParentCategoryIsNull();

        return category.stream()
                .filter(Category::isActive)
                .map(this::mapToResponse)
                .toList();
    }

    public CategoryResponse updateCategory(String newCategoryId, String subCategoryId) {

        Category sub = categoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new NotFoundException("Sub Category not found"));

        Category newCategory = categoryRepository.findById(newCategoryId)
                .orElseThrow(() -> new NotFoundException("New Category not found"));

        if (sub.getCategoryId().equals(newCategory.getCategoryId())) {
            throw new SameException("Category cannot be its own parent");
        }
        if (newCategory.getParentCategory() != null) {
            throw new SameException("New category already has a parent");
        }

        Category oldParent = sub.getParentCategory();

        if (oldParent != null &&
                oldParent.getCategoryId().equals(newCategory.getCategoryId())) {
            throw new SameException("Old Parent Category and New Category must be different");
        }

        if (isDescendant(sub, newCategory)) {
            throw new SameException("Cannot move category. Circular hierarchy detected.");
        }

        if (oldParent != null) {
            oldParent.getSubCategories().remove(sub);
        }

        newCategory.setParentCategory(oldParent);

        if (oldParent != null) {
            oldParent.getSubCategories().add(newCategory);
        }

        sub.setParentCategory(newCategory);
        newCategory.getSubCategories().add(sub);

        categoryRepository.save(newCategory);
        Category savedCategory = categoryRepository.save(sub);

        return mapToResponse(findRoot(savedCategory));
    }

    private boolean isDescendant(Category targetCategory, Category potentialParent) {

        Category current = potentialParent;

        while (current != null) {

            if (current.getCategoryId().equals(targetCategory.getCategoryId())) {
                return true;
            }

            current = current.getParentCategory();
        }

        return false;
    }

    private Category findRoot(Category category) {

        while (category.getParentCategory() != null) {
            category = category.getParentCategory();
        }

        return category;
    }

    public @Nullable CategoryResponse updateCategoryName(String name, String categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        category.setCategoryName(name);

        Category saved = categoryRepository.save(category);

        return mapToResponse(saved);
    }


    public @Nullable CategoryResponse categoryById(String categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (!category.isActive()) {
            throw new NotActivatedException("Category is inactive");
        }

        return mapToResponse(category);
    }


    public MessageDTO deleteCategory(String categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        if(!category.isActive()) throw new AlreadyDoneException("Category is already deactivated");

        category.setActive(false);

        categoryRepository.save(category);

        return new MessageDTO("Category deactivated");
    }

    public @Nullable MessageDTO reactiveCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new NotFoundException("Category not found"));

        if (category.getParentCategory() != null &&
                !category.getParentCategory().isActive()) {
            throw new NotActivatedException("Cannot activate. Parent category is inactive");
        }
        category.setActive(true);
        categoryRepository.save(category);
        return new MessageDTO("Category is reactivated successfully");
    }
}



