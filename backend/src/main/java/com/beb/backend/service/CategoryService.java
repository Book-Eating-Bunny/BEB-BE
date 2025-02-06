package com.beb.backend.service;

import com.beb.backend.domain.Category;
import com.beb.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;


    private Optional<Category> findForeignDetailCategory(String firstDepth, String secondDepth) {
        Optional<Category> rootCategory = categoryRepository
                .findRootCategoryByNameAndMallType(firstDepth, Category.MallType.FOREIGN_BOOK);

        if (rootCategory.isPresent()) {
            return categoryRepository
                    .findCategoryByNameAndParentCategory(secondDepth, rootCategory.get())
                    .or(() -> rootCategory);
        }
        return rootCategory;
    }

    /**
     * 알라딘 API로부터 오는 카테고리명으로부터 DB에 저장된 Category 객체를 찾아 DB에 있으면 반환, 없으면 빈 Optional 반환
     * @param categoryName 알라딘 API 응답의 categoryName
     */
    public Optional<Category> findCategoryByCategoryName(String categoryName) {
        String[] categories = categoryName.split(">");
        if (categories.length <= 1) return Optional.empty();

        Optional<Category.MallType> mallType = Category.MallType.valueOfLabel(categories[0]);
        if (mallType.isEmpty()) {
            log.warn("Undefined Book Category: {}", categoryName);
            return Optional.empty();
        }

        if (mallType.get().getLabel().equals("국내도서") || categories.length == 2) { // 최상위 카테고리만 존재
            return categoryRepository.findRootCategoryByNameAndMallType(categories[1], mallType.get());
        } else {    // 외국도서 중 세부 카테고리 존재할 수 있는 경우
            return findForeignDetailCategory(categories[1], categories[2]);
        }
    }
}
