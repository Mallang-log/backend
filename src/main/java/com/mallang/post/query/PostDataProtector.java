package com.mallang.post.query;

import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import com.mallang.post.query.response.PostDetailResponse;
import com.mallang.post.query.response.PostSearchResponse;
import com.mallang.post.query.response.StaredPostResponse;
import jakarta.annotation.Nullable;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PostDataProtector {

    public PostDetailResponse protectIfRequired(
            @Nullable Long memberId,
            @Nullable String postPassword,
            PostDetailResponse postDetailResponse
    ) {
        if (isNotProtected(postDetailResponse.visibility())) {
            return postDetailResponse;
        }
        if (postDetailResponse.writer().writerId().equals(memberId)) {
            return postDetailResponse;
        }
        if (postDetailResponse.password() != null && Objects.equals(postDetailResponse.password(), postPassword)) {
            return postDetailResponse;
        }
        return new PostDetailResponse(
                postDetailResponse.id(),
                postDetailResponse.blogName(),
                postDetailResponse.title(),
                "보호되어 있는 글입니다. 내용을 보시려면 비밀번호를 입력하세요.",
                "",
                postDetailResponse.visibility(),
                true,
                postDetailResponse.password(),
                postDetailResponse.likeCount(),
                postDetailResponse.isLiked(),
                postDetailResponse.createdDate(),
                postDetailResponse.writer(),
                postDetailResponse.category(),
                postDetailResponse.tags()
        );
    }

    public Page<PostSearchResponse> protectIfRequired(Long memberId, Page<PostSearchResponse> result) {
        return result
                .map(it -> protectIfRequired(memberId, it));
    }

    private PostSearchResponse protectIfRequired(Long memberId, PostSearchResponse postSearchResponse) {
        if (isNotProtected(postSearchResponse.visibility())) {
            return postSearchResponse;
        }
        if (postSearchResponse.writer().writerId().equals(memberId)) {
            return postSearchResponse;
        }
        return new PostSearchResponse(
                postSearchResponse.id(),
                postSearchResponse.blogName(),
                postSearchResponse.title(),
                "보호되어 있는 글입니다.",
                "",
                "",
                postSearchResponse.visibility(),
                postSearchResponse.likeCount(),
                postSearchResponse.createdDate(),
                postSearchResponse.writer(),
                postSearchResponse.category(),
                postSearchResponse.tags()
        );
    }

    public Page<StaredPostResponse> protectStaredIfRequired(
            @Nullable Long requesterId,
            Page<StaredPostResponse> result
    ) {
        return result.map(it -> protectStaredIfRequired(requesterId, it));
    }

    private StaredPostResponse protectStaredIfRequired(
            @Nullable Long requesterId,
            StaredPostResponse data
    ) {
        if (isNotProtected(data.visibility())) {
            return data;
        }
        if (data.writer().writerId().equals(requesterId)) {
            return data;
        }
        return new StaredPostResponse(
                data.starId(),
                data.staredData(),
                data.postId(),
                data.blogName(),
                data.title(),
                "보호되어 있는 글입니다.",
                "",
                "",
                data.visibility(),
                data.postCreatedDate(),
                data.writer(),
                data.category(),
                data.tags()
        );
    }

    private boolean isNotProtected(Visibility visibility) {
        return visibility != Visibility.PROTECTED;
    }
}
