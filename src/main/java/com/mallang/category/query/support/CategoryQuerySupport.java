package com.mallang.category.query.support;

import com.mallang.category.domain.Category;
import com.mallang.category.exception.NotFoundCategoryException;
import com.mallang.common.domain.CommonDomainModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryQuerySupport extends JpaRepository<Category, Long> {

    default List<Long> getCategoryAndDescendants(Long id) {
        Category category = getById(id);
        List<Category> descendants = category.getDescendants();
        descendants.add(category);
        return descendants.stream()
                .map(CommonDomainModel::getId)
                .toList();
    }

    @Override
    default Category getById(Long id) {
        return findById(id).orElseThrow(NotFoundCategoryException::new);
    }

    @Query("SELECT c FROM Category c WHERE c.blog.name.value = :blogName AND c.parent = null")
    List<Category> findAllRootByBlogName(@Param("blogName") String blogName);
}
