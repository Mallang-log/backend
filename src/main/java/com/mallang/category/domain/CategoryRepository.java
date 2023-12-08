package com.mallang.category.domain;

import com.mallang.blog.domain.Blog;
import com.mallang.category.exception.NotFoundCategoryException;
import jakarta.annotation.Nullable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Override
    default Category getById(Long id) {
        return findById(id).orElseThrow(NotFoundCategoryException::new);
    }

    boolean existsByBlog(Blog blog);

    @Query("SELECT c FROM Category c WHERE c.blog = :blog AND c.parent IS NULL")
    List<Category> findAllRootByBlog(@Param("blog") Blog blog);

    default Category getByIdIfIdNotNull(@Nullable Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return getById(categoryId);
    }
}
