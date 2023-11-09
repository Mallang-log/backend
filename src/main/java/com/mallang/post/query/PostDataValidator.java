package com.mallang.post.query;

import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.IncorrectAccessPostException;
import com.mallang.post.exception.NoAuthorityViewPostException;
import com.mallang.post.query.data.PostDetailData;
import org.springframework.stereotype.Component;

@Component
public class PostDataValidator {

    public void validateViewPermissions(Long memberId, PostDetailData postDetailData) {
        if (postDetailData.visibility() != Visibility.PRIVATE) {
            return;
        }
        if (!postDetailData.writerInfo().writerId().equals(memberId)) {
            throw new NoAuthorityViewPostException();
        }
    }

    public void validateAccessProtected(PostDetailData postDetailData, String password) {
        if (postDetailData.visibility() != Visibility.PROTECTED) {
            throw new IncorrectAccessPostException();
        }
        if (!postDetailData.password().equals(password)) {
            throw new NoAuthorityViewPostException();
        }
    }
}
