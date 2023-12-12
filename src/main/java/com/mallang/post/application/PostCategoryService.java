package com.mallang.post.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.post.application.command.CreatePostCategoryCommand;
import com.mallang.post.application.command.DeletePostCategoryCommand;
import com.mallang.post.application.command.UpdatePostCategoryHierarchyCommand;
import com.mallang.post.application.command.UpdatePostCategoryNameCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostCategory;
import com.mallang.post.domain.PostCategoryRepository;
import com.mallang.post.domain.PostCategoryValidator;
import com.mallang.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostCategoryService {

    private final BlogRepository blogRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final PostCategoryValidator postCategoryValidator;

    public Long create(CreatePostCategoryCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Blog blog = blogRepository.getByName(command.blogName());
        PostCategory postCategory = new PostCategory(command.name(), member, blog);
        PostCategory parent = postCategoryRepository.getByIdIfIdNotNull(command.parentId());
        PostCategory prev = postCategoryRepository.getByIdIfIdNotNull(command.prevId());
        PostCategory next = postCategoryRepository.getByIdIfIdNotNull(command.nextId());
        postCategory.create(parent, prev, next, postCategoryValidator);
        return postCategoryRepository.save(postCategory).getId();
    }

    public void updateHierarchy(UpdatePostCategoryHierarchyCommand command) {
        Member member = memberRepository.getById(command.memberId());
        PostCategory target = postCategoryRepository.getById(command.categoryId());
        target.validateOwner(member);
        PostCategory parent = postCategoryRepository.getByIdIfIdNotNull(command.parentId());
        PostCategory prev = postCategoryRepository.getByIdIfIdNotNull(command.prevId());
        PostCategory next = postCategoryRepository.getByIdIfIdNotNull(command.nextId());
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
        postRepository.findAllByCategory(postCategory)
                .forEach(Post::removeCategory);
        postCategoryRepository.delete(postCategory);
    }
}
