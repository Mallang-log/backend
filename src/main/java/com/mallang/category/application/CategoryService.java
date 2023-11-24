package com.mallang.category.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.category.application.command.CreateCategoryCommand;
import com.mallang.category.application.command.DeleteCategoryCommand;
import com.mallang.category.application.command.UpdateCategoryCommand;
import com.mallang.category.domain.Category;
import com.mallang.category.domain.CategoryRepository;
import com.mallang.category.domain.CategoryValidator;
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
        Blog blog = blogRepository.getByIdAndOwnerId(command.blogId(), command.memberId());
        Category parentCategory =
                categoryRepository.getParentByIdAndOwnerId(command.parentCategoryId(), command.memberId());
        Category category = Category.create(command.name(), member, blog, parentCategory, categoryValidator);
        return categoryRepository.save(category).getId();
    }

    public void update(UpdateCategoryCommand command) {
        Category category = categoryRepository.getByIdAndOwnerId(command.categoryId(), command.memberId());
        Category parentCategory =
                categoryRepository.getParentByIdAndOwnerId(command.parentCategoryId(), command.memberId());
        category.update(command.name(), parentCategory, categoryValidator);
    }

    public void delete(DeleteCategoryCommand command) {
        Category category = categoryRepository.getByIdAndOwnerId(command.categoryId(), command.memberId());
        category.delete();
        categoryRepository.delete(category);
    }
}
