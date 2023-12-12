package com.mallang.post.domain.star;

import com.mallang.auth.domain.Member;
import com.mallang.category.CategoryHierarchyViolationException;
import com.mallang.category.TieredCategoryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StarGroupValidator implements TieredCategoryValidator {

    private final StarGroupRepository starGroupRepository;

    @Override
    public void validateNoCategories(Member member) {
        if (starGroupRepository.existsByOwner(member)) {
            throw new CategoryHierarchyViolationException("이미 존재하는 즐겨찾기 그룹이 있습니다.");
        }
    }
}
