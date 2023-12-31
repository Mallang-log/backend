package com.mallang.post.query.repository;

import com.mallang.common.domain.CommonRootEntity;
import com.mallang.post.domain.PostCategory;
import com.mallang.post.exception.NotFoundPostCategoryException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostCategoryQueryRepository extends JpaRepository<PostCategory, Long> {

    default List<Long> getIdsWithDescendants(Long id) {
        PostCategory postCategory = getById(id);
        List<PostCategory> descendants = postCategory.getDescendantsExceptSelf();
        descendants.add(postCategory);
        return descendants.stream()
                .map(CommonRootEntity::getId)
                .toList();
    }

    @Override
    default PostCategory getById(Long id) {
        return findById(id).orElseThrow(NotFoundPostCategoryException::new);
    }

    @Query("SELECT c FROM PostCategory c WHERE c.blog.name.value = :blogName")
    List<PostCategory> findAllByBlogName(@Param("blogName") String blogName);
}
