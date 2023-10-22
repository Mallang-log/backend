package com.mallang.category.domain;

import com.mallang.category.exception.DuplicateCategoryNameException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    public void validateDuplicateRootName(Long memberId, String name) {
        categoryRepository.findAllRootByMemberId(memberId)
                .stream()
                .filter(it -> it.getName().equals(name))
                .findAny()
                .ifPresent(it -> {
                    throw new DuplicateCategoryNameException();
                });
    }
}
