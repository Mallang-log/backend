package com.mallang.category;


import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.domain.Category;
import org.springframework.test.util.ReflectionTestUtils;

public class CategoryFixture {

    public static Long id = 1L;
    public static Long childId = 1L;

    public static Category 루트_카테고리(String name, Member member, Blog blog) {
        return 루트_카테고리(id++, name, member, blog);
    }

    public static Category 루트_카테고리(Long id, String name, Member member, Blog blog) {
        Category category = new Category(name, member, blog);
        ReflectionTestUtils.setField(category, "id", id);
        return category;
    }

    public static Category 하위_카테고리(String name, Member member, Blog blog, Category parent) {
        return 하위_카테고리(parent.getId() + childId++, name, member, blog, parent);
    }

    public static Category 하위_카테고리(
            String name,
            Member member,
            Blog blog,
            Category parent,
            Category prev,
            Category next
    ) {
        return 하위_카테고리(parent.getId() + childId++, name, member, blog, parent, prev, next);
    }

    public static Category 하위_카테고리(Long id, String name, Member member, Blog blog, Category parent) {
        return 하위_카테고리(id, name, member, blog, parent, null, null);
    }

    public static Category 하위_카테고리(
            Long id,
            String name,
            Member member,
            Blog blog,
            Category parent,
            Category prev,
            Category next
    ) {
        Category category = new Category(name, member, blog);
        category.updateHierarchy(parent, prev, next);
        ReflectionTestUtils.setField(category, "id", id);
        return category;
    }
}
