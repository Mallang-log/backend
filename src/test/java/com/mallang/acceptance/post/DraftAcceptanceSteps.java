package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.post.presentation.request.CreateDraftRequest;
import com.mallang.post.presentation.request.UpdateDraftRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
class DraftAcceptanceSteps {

    public static ExtractableResponse<Response> 임시_글_생성_요청(
            String 세션_ID,
            CreateDraftRequest 요청
    ) {
        return given(세션_ID)
                .body(요청)
                .when()
                .post("/drafts")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 임시_글_수정_요청(
            String 세션_ID,
            Long 임시_글_ID,
            UpdateDraftRequest request
    ) {
        return given(세션_ID)
                .body(request)
                .put("/drafts/{id}", 임시_글_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 임시_글_삭제_요청(String 말랑_세션_ID, Long 임시_글_ID) {
        return given(말랑_세션_ID)
                .delete("/drafts/{id}", 임시_글_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 임시_글_목록_조회_요청(String 말랑_세션_ID, String 블로그_이름) {
        return given(말랑_세션_ID)
                .queryParam("blogName", 블로그_이름)
                .get("/drafts")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 임시_글_단일_조회_요청(String 말랑_세션_ID, Long 임시_글_ID) {
        return given(말랑_세션_ID)
                .get("/drafts/{id}", 임시_글_ID)
                .then()
                .extract();
    }
}
