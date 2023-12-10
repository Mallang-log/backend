package com.mallang.post.domain.category;

import com.mallang.blog.domain.Blog;
import com.mallang.post.exception.NotFoundPostCategoryException;
import jakarta.annotation.Nullable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {

    @Override
    default PostCategory getById(Long id) {
        return findById(id).orElseThrow(NotFoundPostCategoryException::new);
    }

    boolean existsByBlog(Blog blog);

    @Query("SELECT c FROM PostCategory c WHERE c.blog = :blog AND c.parent IS NULL")
    List<PostCategory> findAllRootByBlog(@Param("blog") Blog blog);

    default PostCategory getByIdIfIdNotNull(@Nullable Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return getById(categoryId);
    }
}
