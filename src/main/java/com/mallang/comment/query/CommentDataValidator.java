package com.mallang.comment.query;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentDataValidator {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public void validateAccessPost(Long postId,
                                   String blogName,
                                   @Nullable Long memberId,
                                   @Nullable String postPassword) {
        Post post = postRepository.getByIdAndBlogName(postId, blogName);
        Member member = null;
        if (memberId != null) {
            member = memberRepository.getById(memberId);
        }
        post.validatePostAccessibility(member, postPassword);
    }
}
