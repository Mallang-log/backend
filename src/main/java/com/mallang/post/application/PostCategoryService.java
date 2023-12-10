package com.mallang.post.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.post.application.command.CreatePostCategoryCommand;
import com.mallang.post.application.command.DeletePostCategoryCommand;
import com.mallang.post.application.command.UpdatePostCategoryHierarchyCommand;
import com.mallang.post.application.command.UpdatePostCategoryNameCommand;
import com.mallang.post.domain.PostCategory;
import com.mallang.post.domain.PostCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostCategoryService {

    private final BlogRepository blogRepository;
    private final MemberRepository memberRepository;
    private final PostCategoryRepository postCategoryRepository;

    public Long create(CreatePostCategoryCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Blog blog = blogRepository.getByName(command.blogName());
        PostCategory postCategory = new PostCategory(command.name(), member, blog);
        updateHierarchy(postCategory, command.parentId(), command.prevId(), command.nextId());
        return postCategoryRepository.save(postCategory).getId();
    }

    public void updateHierarchy(UpdatePostCategoryHierarchyCommand command) {
        Member member = memberRepository.getById(command.memberId());
        PostCategory target = postCategoryRepository.getById(command.categoryId());
        target.validateOwner(member);
        updateHierarchy(target, command.parentId(), command.prevId(), command.nextId());
    }

    private void updateHierarchy(PostCategory target, Long parentId, Long prevId, Long nextId) {
        PostCategory parent = postCategoryRepository.getByIdIfIdNotNull(parentId);
        PostCategory prev = postCategoryRepository.getByIdIfIdNotNull(prevId);
        PostCategory next = postCategoryRepository.getByIdIfIdNotNull(nextId);
        target.updateHierarchy(parent, prev, next);
    }

    public void updateName(UpdatePostCategoryNameCommand command) {
        Member member = memberRepository.getById(command.memberId());
        PostCategory postCategory = postCategoryRepository.getById(command.categoryId());
        postCategory.validateOwner(member);
        postCategory.updateName(command.name());
    }

    public void delete(DeletePostCategoryCommand command) {
        Member member = memberRepository.getById(command.memberId());
        PostCategory postCategory = postCategoryRepository.getById(command.categoryId());
        postCategory.validateOwner(member);
        postCategory.delete();
        postCategoryRepository.delete(postCategory);
    }
}
