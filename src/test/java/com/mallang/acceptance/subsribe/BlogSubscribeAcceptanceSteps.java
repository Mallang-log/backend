package com.mallang.acceptance.subsribe;

import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.subscribe.presentation.request.BlogSubscribeRequest;
import com.mallang.subscribe.presentation.request.BlogUnsubscribeRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class BlogSubscribeAcceptanceSteps {

    public static ExtractableResponse<Response> 블로그_구독_요청(
            String 세션_ID,
            Long 블로그_ID
    ) {
        return given(세션_ID)
                .body(new BlogSubscribeRequest(블로그_ID))
                .post("/blog-subscribes")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 블로그_구독_취소_요청(
            String 세션_ID,
            Long 블로그_ID
    ) {
        return given(세션_ID)
                .body(new BlogUnsubscribeRequest(블로그_ID))
                .delete("/blog-subscribes")
                .then().log().all()
                .extract();
    }
}
