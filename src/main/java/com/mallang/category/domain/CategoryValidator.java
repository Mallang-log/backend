package com.mallang.category.domain;

import com.mallang.category.exception.DuplicateCategoryNameException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    public void validateDuplicateRootName(Long memberId, String name) {
        List<Category> rootCategories = categoryRepository.findAllRootByMemberId(memberId);
        boolean duplicate = rootCategories.stream()
                .anyMatch(it -> it.getName().equals(name));
        if (duplicate) {
            throw new DuplicateCategoryNameException();
        }
    }
}
