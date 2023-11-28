package com.mallang.acceptance.statistics;

import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.statistics.api.query.PeriodType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class StatisticManageAcceptanceSteps {

    public static ExtractableResponse<Response> 포스트_통계_조회_요청(
            String 세션_ID,
            String 블로그_이름,
            Long 포스트_ID,
            PeriodType periodType,
            String 조회_마지막_일,
            int 조회_개수
    ) {
        return given(세션_ID)
                .param("periodType", periodType)
                .param("lastDay", 조회_마지막_일)
                .param("count", 조회_개수)
                .get("/manage/statistics/posts/{blogName}/{id}", 블로그_이름, 포스트_ID)
                .then().log().all()
                .extract();
    }
}
