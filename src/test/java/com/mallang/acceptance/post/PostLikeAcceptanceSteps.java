package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.given;
import static com.mallang.post.presentation.support.PostPresentationConstant.POST_PASSWORD_COOKIE;

import com.mallang.post.presentation.request.CancelPostLikeRequest;
import com.mallang.post.presentation.request.ClickPostLikeRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import jakarta.annotation.Nullable;

@SuppressWarnings("NonAsciiCharacters")
public class PostLikeAcceptanceSteps {

    public static ExtractableResponse<Response> 포스트_좋아요_요청(
            String 세션_ID,
            Long 포스트_ID,
            String 블로그_이름,
            @Nullable String 비밀번호
    ) {
        return given(세션_ID)
                .cookie(POST_PASSWORD_COOKIE, 비밀번호)
                .body(new ClickPostLikeRequest(포스트_ID, 블로그_이름))
                .post("/post-likes")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 좋아요_취소_요청(
            String 세션_ID,
            Long 포스트_ID,
            String 블로그_이름,
            @Nullable String 비밀번호
    ) {
        return given(세션_ID)
                .cookie(POST_PASSWORD_COOKIE, 비밀번호)
                .body(new CancelPostLikeRequest(포스트_ID, 블로그_이름))
                .delete("/post-likes")
                .then()
                .extract();
    }
}
