package com.mallang.post.application;

import com.mallang.category.domain.CategoryRepository;
import com.mallang.member.domain.Member;
import com.mallang.member.domain.MemberRepository;
import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.application.command.UpdatePostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    public Long create(CreatePostCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Post post = command.toPost(member);
        Optional.ofNullable(command.categoryId())
                .map(categoryRepository::getById)
                .ifPresent(post::setCategory);
        Post saved = postRepository.save(post);
        return saved.getId();
    }

    public void update(UpdatePostCommand command) {
        Post post = postRepository.getById(command.postId());
        post.update(command.memberId(), command.title(), command.content());
    }
}
