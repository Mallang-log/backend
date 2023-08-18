package com.mallang.category.query.repository;

import com.mallang.category.domain.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryQueryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.member.id = :memberId AND c.parent = null")
    List<Category> findAllRootByMemberId(@Param("memberId") Long memberId);
}
