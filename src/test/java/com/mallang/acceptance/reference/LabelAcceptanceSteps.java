package com.mallang.acceptance.reference;

import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.reference.presentation.request.CreateLabelRequest;
import com.mallang.reference.presentation.request.UpdateLabelAttributeRequest;
import com.mallang.reference.presentation.request.UpdateLabelHierarchyRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class LabelAcceptanceSteps {

    public static ExtractableResponse<Response> 라벨_생성_요청(
            String 세션_ID,
            CreateLabelRequest request
    ) {
        return given(세션_ID)
                .body(request)
                .post("/labels")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 라벨_계층구조_수정_요청(
            String 세션_ID,
            Long 라벨_ID,
            UpdateLabelHierarchyRequest request
    ) {
        return given(세션_ID)
                .body(request)
                .put("/labels/{id}/hierarchy", 라벨_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 라벨_속성_수정_요청(
            String 세션_ID,
            Long 라벨_ID,
            UpdateLabelAttributeRequest request
    ) {
        return given(세션_ID)
                .body(request)
                .put("/labels/{id}/attributes", 라벨_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 라벨_제거_요청(
            String 세션_ID,
            Long 라벨_ID
    ) {
        return given(세션_ID)
                .delete("/labels/{id}", 라벨_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 나의_라벨_조회_요청(String 세션) {
        return given(세션)
                .get("/labels")
                .then()
                .extract();
    }
}
