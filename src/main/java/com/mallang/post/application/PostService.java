package com.mallang.post.application;

import com.mallang.member.domain.Member;
import com.mallang.member.domain.MemberRepository;
import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.application.command.UpdatePostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public Long create(CreatePostCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Post post = command.toPost(member);
        Post saved = postRepository.save(post);
        return saved.getId();
    }

    public void update(UpdatePostCommand command) {
        Post post = postRepository.getById(command.postId());
        post.update(command.memberId(), command.title(), command.content());
    }
}
