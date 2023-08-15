package com.mallang.category.application.command;

import com.mallang.category.domain.Category;
import com.mallang.category.domain.CategoryValidator;
import com.mallang.member.domain.Member;
import lombok.Builder;

@Builder
public record CreateCategoryCommand(
        Long memberId,
        String name,
        Long parentCategoryId
) {
    public Category toCategory(Member member, Category parent, CategoryValidator validator) {
        return Category.create(name, member, parent, validator);
    }
}
