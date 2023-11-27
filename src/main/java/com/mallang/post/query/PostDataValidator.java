package com.mallang.post.query;

import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import com.mallang.post.query.response.PostDetailResponse;
import org.springframework.stereotype.Component;

@Component
public class PostDataValidator {

    public void validateAccessPost(Long memberId, PostDetailResponse postDetailResponse) {
        if (postDetailResponse.visibility() != Visibility.PRIVATE) {
            return;
        }
        if (!postDetailResponse.writer().writerId().equals(memberId)) {
            throw new NoAuthorityAccessPostException();
        }
    }
}
