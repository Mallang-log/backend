package com.mallang.post.application;

import com.mallang.category.domain.Category;
import com.mallang.category.domain.CategoryRepository;
import com.mallang.member.domain.Member;
import com.mallang.member.domain.MemberRepository;
import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.application.command.UpdatePostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    public Long create(CreatePostCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Category category = getCategoryByIdIfPresent(command.categoryId());
        Post post = command.toPost(member, category);
        Post saved = postRepository.save(post);
        return saved.getId();
    }

    private Category getCategoryByIdIfPresent(@Nullable Long id) {
        if (id == null) {
            return null;
        }
        return categoryRepository.getById(id);
    }

    public void update(UpdatePostCommand command) {
        Post post = postRepository.getById(command.postId());
        Category category = getCategoryByIdIfPresent(command.categoryId());
        post.update(command.memberId(), command.title(), command.content(), category, command.tags());
    }
}
