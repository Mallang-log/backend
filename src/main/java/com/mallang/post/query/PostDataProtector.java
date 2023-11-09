package com.mallang.post.query;

import com.mallang.post.domain.visibility.PostVisibility.Visibility;
import com.mallang.post.query.data.PostDetailData;
import org.springframework.stereotype.Component;

@Component
public class PostDataProtector {

    public PostDetailData protectIfRequired(Long memberId, PostDetailData postDetailData) {
        if (postDetailData.visibility() != Visibility.PROTECTED) {
            return postDetailData;
        }
        if (postDetailData.writerInfo().writerId().equals(memberId)) {
            return postDetailData;
        }
        return new PostDetailData(
                postDetailData.id(),
                postDetailData.title(),
                "보호되어 있는 글입니다. 내용을 보시려면 비밀번호를 입력하세요.",
                postDetailData.visibility(),
                true,
                postDetailData.likeCount(),
                postDetailData.isLiked(),
                postDetailData.createdDate(),
                postDetailData.writerInfo(),
                postDetailData.categoryInfo(),
                postDetailData.tagDetailInfos()
        );
    }
}
