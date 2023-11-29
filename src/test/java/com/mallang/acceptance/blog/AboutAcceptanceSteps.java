package com.mallang.acceptance.blog;

import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.blog.presentation.request.UpdateAboutRequest;
import com.mallang.blog.presentation.request.WriteAboutRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class AboutAcceptanceSteps {

    public static ExtractableResponse<Response> 블로그_소개_작성_요청(
            String 세션_ID,
            WriteAboutRequest 소개_작성_요청
    ) {
        return given(세션_ID)
                .body(소개_작성_요청)
                .post("/abouts")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 블로그_소개_수정_요청(
            String 세션_ID,
            Long 블로그_소개_ID,
            UpdateAboutRequest 소개_수정_요청
    ) {
        return given(세션_ID)
                .body(소개_수정_요청)
                .put("/abouts/{id}", 블로그_소개_ID)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 블로그_소개_삭제_요청(
            String 세션_ID,
            Long 블로그_소개_ID
    ) {
        return given(세션_ID)
                .delete("/abouts/{id}", 블로그_소개_ID)
                .then().log().all()
                .extract();
    }
}
