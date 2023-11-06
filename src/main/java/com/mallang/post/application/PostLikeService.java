package com.mallang.post.application;

import com.mallang.member.domain.Member;
import com.mallang.member.domain.MemberRepository;
import com.mallang.post.application.command.CancelPostLikeCommand;
import com.mallang.post.application.command.ClickPostLikeCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostLike;
import com.mallang.post.domain.PostLikeRepository;
import com.mallang.post.domain.PostLikeValidator;
import com.mallang.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostLikeService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostLikeValidator postLikeValidator;

    public void click(ClickPostLikeCommand command) {
        Post post = postRepository.getById(command.postId());
        Member member = memberRepository.getById(command.memberId());
        PostLike postLike = new PostLike(post, member);
        postLike.click(postLikeValidator);
        postLikeRepository.save(postLike);
    }

    public void cancel(CancelPostLikeCommand command) {
        PostLike postLike = postLikeRepository.getByPostIdAndMemberId(command.postId(), command.memberId());
        postLike.cancel();
    }
}
