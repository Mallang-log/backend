package com.mallang.post.domain;

import com.mallang.auth.domain.Member;
import com.mallang.post.exception.AlreadyLikedPostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostLikeValidator {

    private final PostLikeRepository postLikeRepository;

    public void validateClickLike(Post post, Member member) {
        if (postLikeRepository.existsByPostAndMember(post, member)) {
            throw new AlreadyLikedPostException();
        }
    }
}
