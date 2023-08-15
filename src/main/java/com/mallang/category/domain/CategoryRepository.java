package com.mallang.category.domain;

import com.mallang.category.exception.NotFoundCategoryException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Override
    default Category getById(Long id) {
        return findById(id).orElseThrow(NotFoundCategoryException::new);
    }

    @Query("SELECT c FROM Category c WHERE c.member.id = :memberId AND c.parent = null")
    List<Category> findAllRootByMemberId(@Param("memberId") Long memberId);
}
