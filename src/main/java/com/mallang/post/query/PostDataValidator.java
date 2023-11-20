package com.mallang.post.query;

import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import com.mallang.post.query.data.PostDetailData;
import org.springframework.stereotype.Component;

@Component
public class PostDataValidator {

    public void validateAccessPost(Long memberId, PostDetailData postDetailData) {
        if (postDetailData.visibility() != Visibility.PRIVATE) {
            return;
        }
        if (!postDetailData.writerInfo().writerId().equals(memberId)) {
            throw new NoAuthorityAccessPostException();
        }
    }
}
