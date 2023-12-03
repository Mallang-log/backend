package com.mallang.acceptance.reference;

import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.reference.presentation.request.SaveReferenceLinkRequest;
import com.mallang.reference.presentation.request.UpdateReferenceLinkRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class ReferenceLinkAcceptanceSteps {

    public static ExtractableResponse<Response> 참조_링크_저장_요청(
            String 세션_ID,
            String 블로그_이름,
            SaveReferenceLinkRequest 참조_링크_저장_요청
    ) {
        return given(세션_ID)
                .body(참조_링크_저장_요청)
                .post("/reference-links/{blogName}", 블로그_이름)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 참조_링크_업데이트_요청(
            String 세션_ID,
            Long 참조_링크_ID,
            UpdateReferenceLinkRequest 참조_링크_업데이트_요청
    ) {
        return given(세션_ID)
                .body(참조_링크_업데이트_요청)
                .put("/reference-links/{referenceLinkId}", 참조_링크_ID)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 참조_링크_삭제_요청(
            String 세션_ID,
            Long 참조_링크_ID
    ) {
        return given(세션_ID)
                .delete("/reference-links/{referenceLinkId}", 참조_링크_ID)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> URL_의_제목_추출_요청(String 세션_ID, String URL) {
        return given(세션_ID)
                .queryParam("url", URL)
                .get("/reference-links/title-info")
                .then().log().all()
                .extract();
    }
}
