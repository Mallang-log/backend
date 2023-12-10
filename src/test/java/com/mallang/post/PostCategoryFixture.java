package com.mallang.post;


import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.post.domain.category.PostCategory;
import org.springframework.test.util.ReflectionTestUtils;

public class PostCategoryFixture {

    public static Long id = 1L;
    public static Long childId = 1L;

    public static PostCategory 루트_카테고리(String name, Member member, Blog blog) {
        return 루트_카테고리(id++, name, member, blog);
    }

    public static PostCategory 루트_카테고리(Long id, String name, Member member, Blog blog) {
        PostCategory postCategory = new PostCategory(name, member, blog);
        ReflectionTestUtils.setField(postCategory, "id", id);
        return postCategory;
    }

    public static PostCategory 하위_카테고리(String name, Member member, Blog blog, PostCategory parent) {
        return 하위_카테고리(parent.getId() + childId++, name, member, blog, parent);
    }

    public static PostCategory 하위_카테고리(
            String name,
            Member member,
            Blog blog,
            PostCategory parent,
            PostCategory prev,
            PostCategory next
    ) {
        return 하위_카테고리(parent.getId() + childId++, name, member, blog, parent, prev, next);
    }

    public static PostCategory 하위_카테고리(Long id, String name, Member member, Blog blog, PostCategory parent) {
        return 하위_카테고리(id, name, member, blog, parent, null, null);
    }

    public static PostCategory 하위_카테고리(
            Long id,
            String name,
            Member member,
            Blog blog,
            PostCategory parent,
            PostCategory prev,
            PostCategory next
    ) {
        PostCategory postCategory = new PostCategory(name, member, blog);
        postCategory.updateHierarchy(parent, prev, next);
        ReflectionTestUtils.setField(postCategory, "id", id);
        return postCategory;
    }
}
