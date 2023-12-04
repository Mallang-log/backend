package com.mallang.acceptance.statistics;

import static com.mallang.acceptance.AcceptanceSteps.given;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class StatisticAcceptanceSteps {

    public static ExtractableResponse<Response> 블로그_방문자_통계_조회_요청(
            String 블로그_이름,
            String 오늘
    ) {
        return given()
                .param("today", 오늘)
                .get("/statistics/blogs/{blogName}", 블로그_이름)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 포스트_누적_조회수_조회_요청(
            String 블로그_이름,
            Long 포스트_ID
    ) {
        return given()
                .get("/statistics/posts/{blogName}/{id}", 블로그_이름, 포스트_ID)
                .then()
                .extract();
    }
}
