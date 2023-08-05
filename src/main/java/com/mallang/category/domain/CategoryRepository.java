package com.mallang.category.domain;

import com.mallang.category.application.exception.NotFoundCategoryException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Override
    default Category getById(Long id) {
        return findById(id).orElseThrow(NotFoundCategoryException::new);
    }
}
