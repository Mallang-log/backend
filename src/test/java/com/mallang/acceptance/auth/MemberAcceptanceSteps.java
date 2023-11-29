package com.mallang.acceptance.auth;

import static com.mallang.acceptance.AcceptanceSteps.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.auth.presentation.request.BasicLoginRequest;
import com.mallang.auth.presentation.request.BasicSignupRequest;
import com.mallang.auth.query.response.MemberResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class MemberAcceptanceSteps {

    public static ExtractableResponse<Response> 일반_회원가입_요청(BasicSignupRequest request) {
        return given()
                .body(request)
                .post("/members")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 일반_로그인_요청(
            String 아이디,
            String 비밀번호
    ) {
        return given()
                .body(new BasicLoginRequest(아이디, 비밀번호))
                .post("/members/login")
                .then().log().all()
                .extract();
    }

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

    public static MemberResponse 회원_정보_조회_결과_데이터(
            ExtractableResponse<Response> 응답
    ) {
        return 응답.response().as(MemberResponse.class);
    }

    public static void 회원_정보_조회_결과를_검증한다(
            ExtractableResponse<Response> 응답,
            MemberResponse 예상
    ) {
        MemberResponse 회원_정보 = 응답.response().as(MemberResponse.class);
        assertThat(회원_정보)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(예상);
    }
}
