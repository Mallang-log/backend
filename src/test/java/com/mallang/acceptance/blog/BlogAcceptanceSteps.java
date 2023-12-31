package com.mallang.acceptance.blog;

import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.blog.presentation.request.OpenBlogRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class BlogAcceptanceSteps {

    public static String 블로그_개설(
            String 세션_ID,
            String 블로그_이름
    ) {
        블로그_개설_요청(세션_ID, 블로그_이름);
        return 블로그_이름;
    }

    public static ExtractableResponse<Response> 블로그_개설_요청(
            String 세션_ID,
            String 블로그_이름
    ) {
        return given(세션_ID)
                .body(new OpenBlogRequest(블로그_이름))
                .post("/blogs")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 블로그_이름_중복_확인_요청(String 이름) {
        return given()
                .queryParam("blogName", 이름)
                .get("/blogs/duplicate")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 블로그_정보_조회_요청(String 블로그_이름) {
        return given()
                .queryParam("blogName", 블로그_이름)
                .get("/blogs")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 내_블로그_정보_조회_요청(String 세션) {
        return given(세션)
                .get("/blogs/my")
                .then()
                .extract();
    }
}
