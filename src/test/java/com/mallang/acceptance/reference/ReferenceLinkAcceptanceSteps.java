package com.mallang.acceptance.reference;

import static com.mallang.acceptance.AcceptanceSteps.given;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class ReferenceLinkAcceptanceSteps {

    public static ExtractableResponse<Response> URL_의_제목_추출_요청(String 세션_ID, String URL) {
        return given(세션_ID)
                .queryParam("url", URL)
                .get("/reference-links/title-info")
                .then()
                .extract();
    }
}
