package com.mallang.category.domain;

import com.mallang.category.exception.NotFoundCategoryException;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Override
    default Category getById(Long id) {
        return findById(id).orElseThrow(NotFoundCategoryException::new);
    }

    @Nullable
    default Category getParentByIdAndOwner(@Nullable Long parentCategoryId, Long memberId) {
        if (parentCategoryId == null) {
            return null;
        }
        return getByIdAndOwner(parentCategoryId, memberId);
    }

    default Category getByIdAndOwner(Long id, Long ownerId) {
        return findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() ->
                        new NotFoundCategoryException("존재하지 않는 카테고리거나, 해당 사용자의 카테고리가 아닙니다."));
    }

    Optional<Category> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("SELECT c FROM Category c WHERE c.owner.id = :memberId AND c.parent = null")
    List<Category> findAllRootByMemberId(@Param("memberId") Long memberId);
}
