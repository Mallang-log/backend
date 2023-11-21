package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.given;
import static com.mallang.post.presentation.support.PostPresentationConstant.POST_PASSWORD_COOKIE;

import com.mallang.post.presentation.request.CancelPostStarRequest;
import com.mallang.post.presentation.request.StarPostRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import jakarta.annotation.Nullable;

@SuppressWarnings("NonAsciiCharacters")
public class PostStarAcceptanceSteps {

    public static ExtractableResponse<Response> 포스트_즐겨찾기_요청(String 세션_ID, Long 포스트_ID, @Nullable String 비밀번호) {
        return given(세션_ID)
                .cookie(POST_PASSWORD_COOKIE, 비밀번호)
                .body(new StarPostRequest(포스트_ID))
                .post("/post-stars")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 포스트_즐겨찾기_취소_요청(String 세션_ID, Long 포스트_ID) {
        return given(세션_ID)
                .body(new CancelPostStarRequest(포스트_ID))
                .delete("/post-stars")
                .then().log().all()
                .extract();
    }
}
