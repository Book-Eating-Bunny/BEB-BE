package com.beb.backend.repository;

import com.beb.backend.domain.Category;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c from Category c "
            + "WHERE c.parentCategory IS NULL AND c.name = :name AND c.mallType = :mallType")
    Optional<Category> findRootCategoryByNameAndMallType(@Param("name") String name,
                                                         @Param("mallType") Category.MallType mallType);

    Optional<Category> findCategoryByNameAndParentCategory(@NotNull String name, Category parentCategory);
}
