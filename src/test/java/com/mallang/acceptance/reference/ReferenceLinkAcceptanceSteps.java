package com.mallang.acceptance.reference;

import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.reference.presentation.request.SaveReferenceLinkRequest;
import com.mallang.reference.presentation.request.UpdateReferenceLinkRequest;
import com.mallang.reference.query.repository.ReferenceLinkSearchDao.ReferenceLinkSearchDaoCond;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class ReferenceLinkAcceptanceSteps {

    public static ExtractableResponse<Response> 참고_링크_저장_요청(
            String 세션_ID,
            SaveReferenceLinkRequest 참고_링크_저장_요청
    ) {
        return given(세션_ID)
                .body(참고_링크_저장_요청)
                .post("/reference-links")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 참고_링크_업데이트_요청(
            String 세션_ID,
            Long 참고_링크_ID,
            UpdateReferenceLinkRequest 참고_링크_업데이트_요청
    ) {
        return given(세션_ID)
                .body(참고_링크_업데이트_요청)
                .put("/reference-links/{referenceLinkId}", 참고_링크_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 참고_링크_삭제_요청(
            String 세션_ID,
            Long 참고_링크_ID
    ) {
        return given(세션_ID)
                .delete("/reference-links/{referenceLinkId}", 참고_링크_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 주어진_URL_로_이미_등록된_링크_존재여부_확인_요청(
            String 세션_ID,
            String URL
    ) {
        return given(세션_ID)
                .queryParam("url", URL)
                .get("/reference-links/exists")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 참고_링크_검색_요청(
            String 세션_ID,
            ReferenceLinkSearchDaoCond 검색_조건
    ) {
        return given(세션_ID)
                .queryParam("url", 검색_조건.url())
                .queryParam("title", 검색_조건.title())
                .queryParam("memo", 검색_조건.memo())
                .queryParam("labelId", 검색_조건.labelId())
                .get("/reference-links")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> URL_의_제목_추출_요청(String 세션_ID, String URL) {
        return given(세션_ID)
                .queryParam("url", URL)
                .get("/reference-links/title-info")
                .then()
                .extract();
    }
}
