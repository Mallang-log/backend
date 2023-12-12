package com.mallang.post.domain;

import com.mallang.auth.domain.Member;
import com.mallang.category.CategoryHierarchyViolationException;
import com.mallang.category.TieredCategoryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostCategoryValidator implements TieredCategoryValidator {

    private final PostCategoryRepository postCategoryRepository;

    @Override
    public void validateNoCategories(Member member) {
        if (postCategoryRepository.existsByOwner(member)) {
            throw new CategoryHierarchyViolationException("이미 존재하는 카테고리가 있습니다.");
        }
    }
}
