package com.mallang.acceptance.blog;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.blog.presentation.request.OpenBlogRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class BlogAcceptanceSteps {

    public static Long 블로그_개설(
            String 세션_ID,
            String 블로그_이름
    ) {
        return ID를_추출한다(블로그_개설_요청(세션_ID, 블로그_이름));
    }

    public static ExtractableResponse<Response> 블로그_개설_요청(
            String 세션_ID,
            String 블로그_이름
    ) {
        return given(세션_ID)
                .body(new OpenBlogRequest(블로그_이름))
                .post("/blogs")
                .then().log().all()
                .extract();
    }
}
