package com.mallang.category.application;

import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.category.application.command.CreateCategoryCommand;
import com.mallang.category.application.command.DeleteCategoryCommand;
import com.mallang.category.application.command.UpdateCategoryCommand;
import com.mallang.category.domain.Category;
import com.mallang.category.domain.CategoryRepository;
import com.mallang.category.domain.CategoryValidator;
import com.mallang.member.domain.Member;
import com.mallang.member.domain.MemberRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CategoryService {

    private final MemberRepository memberRepository;
    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryValidator categoryValidator;

    public Long create(CreateCategoryCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Blog blog = blogRepository.getByName(command.blogName());
        Category parentCategory = getParentCategory(command.parentCategoryId());
        Category category = Category.create(command.name(), member, blog, parentCategory, categoryValidator);
        return categoryRepository.save(category).getId();
    }

    private @Nullable Category getParentCategory(@Nullable Long parentCategoryId) {
        if (parentCategoryId == null) {
            return null;
        }
        return categoryRepository.getById(parentCategoryId);
    }

    public void update(UpdateCategoryCommand command) {
        Category category = categoryRepository.getByIdAndBlogName(command.categoryId(), command.blogName());
        Category parentCategory = getParentCategory(command.parentCategoryId());
        category.update(command.memberId(), command.name(), parentCategory, categoryValidator);
    }

    public void delete(DeleteCategoryCommand command) {
        Category category = categoryRepository.getByIdAndBlogName(command.categoryId(), command.blogName());
        category.delete(command.memberId());
        categoryRepository.delete(category);
    }
}
