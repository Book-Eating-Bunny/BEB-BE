package com.beb.backend.repository;

import com.beb.backend.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("select count(c) > 0 from Category c "
            + "where c.name = :name and c.parentCategory.id = :parentId and c.mallType = :mallType")
    boolean existsByNameAndParentIdAndMallType(@Param("name") String name,
                                               @Param("parentId") Long parentId,
                                               @Param("mallType") Category.MallType mallType);

    @Query("select c.id from Category c "
            + "where c.parentCategory is null and c.name = :name and c.mallType = :mallType")
    Long findRootCategoryIdByNameAndMallType(@Param("name") String name,
                                             @Param("mallType") Category.MallType mallType);

    @Query("select c.id from Category c "
            + "where c.name = :name and c.parentCategory.id = :parentId")
    Long findCategoryIdByNameAndParentId(@Param("name") String name, @Param("parentId") Long parentId);
}
