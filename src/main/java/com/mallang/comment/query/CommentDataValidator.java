package com.mallang.comment.query;

import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;

import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentDataValidator {

    private final PostRepository postRepository;

    public void validateAccessPost(Long postId,
                                   @Nullable Long memberId,
                                   @Nullable String postPassword) {
        Post post = postRepository.getById(postId);
        PostVisibilityPolicy visibilityPolish = post.getVisibilityPolish();
        Visibility visibility = visibilityPolish.getVisibility();
        if (visibility == PUBLIC) {
            return;
        }
        if (post.getWriter().getId().equals(memberId)) {
            return;
        }
        if (visibility == PROTECTED && (visibilityPolish.getPassword().equals(postPassword))) {
            return;
        }
        throw new NoAuthorityAccessPostException();
    }
}
