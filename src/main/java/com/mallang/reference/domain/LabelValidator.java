package com.mallang.reference.domain;

import com.mallang.auth.domain.Member;
import com.mallang.category.CategoryHierarchyViolationException;
import com.mallang.category.DuplicateCategoryNameException;
import com.mallang.category.FlatCategoryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LabelValidator implements FlatCategoryValidator {

    private final LabelRepository labelRepository;

    @Override
    public void validateNoCategories(Member member) {
        if (labelRepository.existsByOwner(member)) {
            throw new CategoryHierarchyViolationException("이미 존재하는 카테고리가 있습니다.");
        }
    }

    @Override
    public void validateDuplicateName(Member member, String name) {
        if (labelRepository.existsByOwnerAndName(member, name)) {
            throw new DuplicateCategoryNameException("'%s' 이름이 이미 사용중입니다.".formatted(name));
        }
    }
}
