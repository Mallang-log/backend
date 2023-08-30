package com.mallang.category.application;

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
    private final CategoryRepository categoryRepository;
    private final CategoryValidator categoryValidator;

    public Long create(CreateCategoryCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Category parentCategory = getParentCategory(command.parentCategoryId());
        Category category = Category.create(command.name(), member, parentCategory, categoryValidator);
        return categoryRepository.save(category).getId();
    }

    private Category getParentCategory(@Nullable Long parentCategoryId) {
        if (parentCategoryId == null) {
            return null;
        }
        return categoryRepository.getById(parentCategoryId);
    }

    public void update(UpdateCategoryCommand command) {
        Category category = categoryRepository.getById(command.categoryId());
        Category parentCategory = getParentCategory(command.parentCategoryId());
        category.update(command.memberId(), command.name(), parentCategory, categoryValidator);
    }

    public void delete(DeleteCategoryCommand command) {
        Category category = categoryRepository.getById(command.categoryId());
        category.delete(command.memberId());
        categoryRepository.delete(category);
    }
}
