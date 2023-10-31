package com.mallang.category.query.dao.support;

import com.mallang.blog.domain.BlogName;
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

    @Query("SELECT c FROM Category c WHERE c.member.id = :memberId AND c.parent = null AND c.blog.name = :blogName")
    List<Category> findAllRootByMemberIdAndBlogName(
            @Param("memberId") Long memberId,
            @Param("blogName") BlogName blogName
    );
}
