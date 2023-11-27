package com.mallang.post.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.post.application.command.CancelPostLikeCommand;
import com.mallang.post.application.command.ClickPostLikeCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.domain.like.PostLike;
import com.mallang.post.domain.like.PostLikeRepository;
import com.mallang.post.domain.like.PostLikeValidator;
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

    public void like(ClickPostLikeCommand command) {
        Post post = postRepository.getByIdAndBlogName(command.postId(), command.blogName());
        Member member = memberRepository.getById(command.memberId());
        PostLike postLike = new PostLike(post, member);
        postLike.like(postLikeValidator, command.postPassword());
        postLikeRepository.save(postLike);
    }

    public void cancel(CancelPostLikeCommand command) {
        PostLike postLike = postLikeRepository
                .getByPostIdAndMemberId(command.postId(), command.blogName(), command.memberId());
        postLike.cancel(command.postPassword());
        postLikeRepository.delete(postLike);
    }
}
