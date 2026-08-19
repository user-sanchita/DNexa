package com.example.ECommerce.Platform.Repository;

import com.example.ECommerce.Platform.Model.Category;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,String> {

    List<Category> findByParentCategoryIsNull();

    boolean existsByCategoryNameAndParentCategory(String categoryName, Category parentCategory);

    boolean existsByCategoryNameAndParentCategoryIsNull(String categoryName);
}
