package com.mallang.post.application.command;

import com.mallang.category.domain.Category;
import com.mallang.member.domain.Member;
import com.mallang.post.domain.Post;
import java.util.List;
import lombok.Builder;

@Builder
public record CreatePostCommand(
        Long memberId,
        String title,
        String content,
        Long categoryId,
        List<String> tags
) {
    public Post toPost(Member member, Category category) {
        return Post.builder()
                .title(title)
                .content(content)
                .member(member)
                .category(category)
                .tags(tags)
                .build();
    }
}
