package com.mallang.post.domain;

import com.mallang.post.exception.NotFoundPostCategoryException;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {

    @Override
    default PostCategory getById(Long id) {
        return findById(id).orElseThrow(NotFoundPostCategoryException::new);
    }

    default PostCategory getByIdIfIdNotNull(@Nullable Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return getById(categoryId);
    }
}
