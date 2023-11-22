package com.mallang.acceptance.subsribe;

import static com.mallang.acceptance.AcceptanceSteps.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.common.presentation.PageResponse;
import com.mallang.subscribe.presentation.request.BlogSubscribeRequest;
import com.mallang.subscribe.presentation.request.BlogUnsubscribeRequest;
import com.mallang.subscribe.query.response.SubscriberResponse;
import com.mallang.subscribe.query.response.SubscribingBlogResponse;
import io.restassured.common.mapper.TypeRef;
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

    public static ExtractableResponse<Response> 특정_회원이_구독중인_블로그_조회_요청(Long 구독자_ID) {
        return given()
                .param("memberId", 구독자_ID)
                .get("/blog-subscribes/subscribing-blogs")
                .then().log().all()
                .extract();
    }

    public static void 구독중인_블로그_조회_결과_검증(
            ExtractableResponse<Response> 응답,
            String... 블로그_이름들
    ) {
        PageResponse<SubscribingBlogResponse> result = 응답.response().as(new TypeRef<>() {
        });
        assertThat(result.content())
                .extracting(SubscribingBlogResponse::blogName)
                .containsExactly(블로그_이름들);
    }

    public static ExtractableResponse<Response> 특정_블로그를_구독중인_구독자_조회_요청(Long 블로그_ID) {
        return given()
                .param("blogId", 블로그_ID)
                .get("/blog-subscribes/subscribers")
                .then().log().all()
                .extract();
    }

    public static void 블로그_구독자_조회_결과_검증(
            ExtractableResponse<Response> 응답,
            String... 구독자_이름들
    ) {
        PageResponse<SubscriberResponse> result = 응답.response().as(new TypeRef<>() {
        });
        assertThat(result.content())
                .extracting(SubscriberResponse::subscriberNickname)
                .containsExactly(구독자_이름들);
    }
}
