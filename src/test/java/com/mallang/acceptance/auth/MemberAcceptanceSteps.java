package com.mallang.acceptance.auth;

import static com.mallang.acceptance.AcceptanceSteps.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.auth.query.data.MemberProfileData;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class MemberAcceptanceSteps {

    public static ExtractableResponse<Response> 내_정보_조회_요청(
            String 세션_ID
    ) {
        return given(세션_ID)
                .get("/members/my")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_정보_조회_요청(
            Long 회원_ID
    ) {
        return given()
                .get("/members/{id}", 회원_ID)
                .then().log().all()
                .extract();
    }

    public static MemberProfileData 회원_정보_조회_결과_데이터(
            ExtractableResponse<Response> 응답
    ) {
        return 응답.response().as(MemberProfileData.class);
    }

    public static void 회원_정보_조회_결과를_검증한다(
            ExtractableResponse<Response> 응답,
            MemberProfileData 예상
    ) {
        MemberProfileData 회원_정보 = 응답.response().as(MemberProfileData.class);
        assertThat(회원_정보)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(예상);
    }
}
