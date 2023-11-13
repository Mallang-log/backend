package com.mallang.post.query;

import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.query.data.PostDetailData;
import com.mallang.post.query.data.PostSimpleData;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PostDataProtector {

    public PostDetailData protectIfRequired(Long memberId, PostDetailData postDetailData) {
        if (isNotProtected(postDetailData.visibility())) {
            return postDetailData;
        }
        if (postDetailData.writerInfo().writerId().equals(memberId)) {
            return postDetailData;
        }
        return new PostDetailData(
                postDetailData.id(),
                postDetailData.title(),
                "보호되어 있는 글입니다. 내용을 보시려면 비밀번호를 입력하세요.",
                "",
                postDetailData.visibility(),
                true,
                postDetailData.password(),
                postDetailData.likeCount(),
                postDetailData.isLiked(),
                postDetailData.createdDate(),
                postDetailData.writerInfo(),
                postDetailData.categoryInfo(),
                postDetailData.tagDetailInfos()
        );
    }

    private boolean isNotProtected(Visibility visibility) {
        return visibility != Visibility.PROTECTED;
    }

    public List<PostSimpleData> protectIfRequired(Long memberId, List<PostSimpleData> result) {
        return result.stream()
                .map(it -> protectIfRequired(memberId, it))
                .toList();
    }

    private PostSimpleData protectIfRequired(Long memberId, PostSimpleData postSimpleData) {
        if (isNotProtected(postSimpleData.visibility())) {
            return postSimpleData;
        }
        if (postSimpleData.writerInfo().writerId().equals(memberId)) {
            return postSimpleData;
        }
        return new PostSimpleData(
                postSimpleData.id(),
                postSimpleData.title(),
                "보호되어 있는 글입니다.",
                "",
                "",
                postSimpleData.visibility(),
                postSimpleData.likeCount(),
                postSimpleData.createdDate(),
                postSimpleData.writerInfo(),
                postSimpleData.categoryInfo(),
                postSimpleData.tagSimpleInfos()
        );
    }
}
