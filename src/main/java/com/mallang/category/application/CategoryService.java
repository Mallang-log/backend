package com.mallang.category.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.category.application.command.CreateCategoryCommand;
import com.mallang.category.application.command.DeleteCategoryCommand;
import com.mallang.category.application.command.UpdateCategoryHierarchyCommand;
import com.mallang.category.application.command.UpdateCategoryNameCommand;
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

    private final BlogRepository blogRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryValidator categoryValidator;

    public Long create(CreateCategoryCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Blog blog = blogRepository.getByName(command.blogName());
        Category category = new Category(command.name(), member, blog);
        updateHierarchy(category, command.parentId(), command.prevId(), command.nextId());
        return categoryRepository.save(category).getId();
    }

    public void updateHierarchy(UpdateCategoryHierarchyCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Category target = categoryRepository.getById(command.categoryId());
        target.validateOwner(member);
        updateHierarchy(target, command.parentId(), command.prevId(), command.nextId());
    }

    private void updateHierarchy(Category target, Long parentId, Long prevId, Long nextId) {
        Category parent = categoryRepository.getByIdIfIdNotNull(parentId);
        Category prev = categoryRepository.getByIdIfIdNotNull(prevId);
        Category next = categoryRepository.getByIdIfIdNotNull(nextId);
        target.updateHierarchy(parent, prev, next, categoryValidator);
    }

    public void updateName(UpdateCategoryNameCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Category category = categoryRepository.getById(command.categoryId());
        category.validateOwner(member);
        category.updateName(command.name(), categoryValidator);
    }

    public void delete(DeleteCategoryCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Category category = categoryRepository.getById(command.categoryId());
        category.validateOwner(member);
        category.delete();
        categoryRepository.delete(category);
    }
}
