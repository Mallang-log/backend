package com.mallang.acceptance.nofitifaction;

import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.common.presentation.PageResponse;
import com.mallang.notification.query.response.NotificationListResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class NotificationAcceptanceSteps {

    public static PageResponse<NotificationListResponse> 내_알림_목록_조회_요청(String 세션) {
        ExtractableResponse<Response> 응답 = given(세션)
                .get("/notifications")
                .then()
                //.log().all()
                .extract();
        return 응답.as(new TypeRef<>() {
        });
    }

    public static ExtractableResponse<Response> 알림_읽을_처리_요청(String 세션, Long 알림_ID) {
        return given(세션)
                .post("/notifications/read/{id}", 알림_ID)
                .then()
                //.log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 알림_제거_요청(String 세션, Long 알림_ID) {
        return given(세션)
                .delete("/notifications/{id}", 알림_ID)
                .then()
                //.log().all()
                .extract();
    }
}
