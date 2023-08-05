package com.mallang.category.application;

import com.mallang.category.application.command.CreateCategoryCommand;
import com.mallang.category.application.command.UpdateCategoryCommand;
import com.mallang.category.domain.Category;
import com.mallang.category.domain.CategoryRepository;
import com.mallang.member.domain.Member;
import com.mallang.member.domain.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    public Long create(CreateCategoryCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Category category = command.toCategory(member);
        Optional.ofNullable(command.parentCategoryId())
                .map(categoryRepository::getById)
                .ifPresent(category::setParent);
        return categoryRepository.save(category).getId();
    }

    public void update(UpdateCategoryCommand command) {
        Category category = categoryRepository.getById(command.categoryId());
        Category parentCategory = Optional.ofNullable(command.parentCategoryId())
                .map(categoryRepository::getById)
                .orElse(null);
        category.update(command.memberId(), command.name(), parentCategory);
    }
}
