package com.mallang.category.application.command;

import com.mallang.category.domain.Category;
import com.mallang.member.domain.Member;

public record CreateCategoryCommand(
        Long memberId,
        String name,
        Long parentCategoryId
) {
    public Category toCategory(Member member) {
        return Category.builder()
                .name(name)
                .member(member)
                .build();
    }
}
