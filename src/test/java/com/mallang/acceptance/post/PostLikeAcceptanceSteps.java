package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.post.presentation.request.CancelPostLikeRequest;
import com.mallang.post.presentation.request.ClickPostLikeRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class PostLikeAcceptanceSteps {

    public static ExtractableResponse<Response> 포스트_좋아요_요청(String 세션_ID, Long 포스트_ID) {
        return given(세션_ID)
                .body(new ClickPostLikeRequest(포스트_ID))
                .post("/post-likes")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 좋아요_취소_요청(String 세션_ID, Long 포스트_ID) {
        return given(세션_ID)
                .body(new CancelPostLikeRequest(포스트_ID))
                .delete("/post-likes")
                .then().log().all()
                .extract();
    }

}
