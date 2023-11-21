package com.mallang.post.domain.star;

import com.mallang.auth.domain.Member;
import com.mallang.post.domain.Post;
import com.mallang.post.exception.AlreadyStarPostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostStarValidator {

    private final PostStarRepository postStarRepository;

    public void validateClickStar(Post post, Member member) {
        if (postStarRepository.existsByPostAndMember(post, member)) {
            throw new AlreadyStarPostException();
        }
    }
}
