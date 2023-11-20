package com.mallang.category.query.dao.support;

import com.mallang.category.domain.Category;
import com.mallang.category.exception.NotFoundCategoryException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryQuerySupport extends JpaRepository<Category, Long> {

    @Override
    default Category getById(Long id) {
        return findById(id).orElseThrow(NotFoundCategoryException::new);
    }

    @Query("SELECT c FROM Category c WHERE c.blog.name.value = :blogName AND c.parent = null")
    List<Category> findAllRootByBlogName(@Param("blogName") String blogName);
}
