package com.mallang.category;

import static com.mallang.category.domain.AlwaysPassCategoryValidator.alwaysPassCategoryValidator;

import com.mallang.category.domain.Category;
import com.mallang.member.domain.Member;
import org.springframework.test.util.ReflectionTestUtils;

public class CategoryFixture {

    public static Category 루트_카테고리(String name, Member member) {
        return 루트_카테고리(null, name, member);
    }

    public static Category 루트_카테고리(Long id, String name, Member member) {
        Category category = Category.create(name, member, null, alwaysPassCategoryValidator);
        ReflectionTestUtils.setField(category, "id", id);
        return category;
    }

    public static Category 하위_카테고리(String name, Member member, Category parent) {
        return 하위_카테고리(null, name, member, parent);
    }

    public static Category 하위_카테고리(Long id, String name, Member member, Category parent) {
        Category category = Category.create(name, member, parent, alwaysPassCategoryValidator);
        ReflectionTestUtils.setField(category, "id", id);
        return category;
    }
}
