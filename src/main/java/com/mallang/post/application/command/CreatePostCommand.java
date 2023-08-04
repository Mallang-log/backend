package com.mallang.post.application.command;

import com.mallang.member.domain.Member;
import com.mallang.post.domain.Post;
import lombok.Builder;

@Builder
public record CreatePostCommand(
        Long memberId,
        String title,
        String content
) {
    public Post toPost(Member member) {
        return Post.builder()
                .title(title)
                .content(content)
                .member(member)
                .build();
    }
}
