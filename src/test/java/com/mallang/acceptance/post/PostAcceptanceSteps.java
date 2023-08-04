package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.post.application.query.PostDetailResponse;
import com.mallang.post.application.query.PostSimpleResponse;
import com.mallang.post.presentation.request.CreatePostRequest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class PostAcceptanceSteps {

    public static ExtractableResponse<Response> 게시글_생성_요청을_보낸다(String 세션_ID, CreatePostRequest createPostRequest) {
        return given(세션_ID)
                .body(createPostRequest)
                .when()
                .post("/posts")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 게시글_단일_조회_요청을_보낸다(Long 게시글_ID) {
        return given()
                .get("/posts/{id}", 게시글_ID)
                .then().log().all()
                .extract();
    }

    public static void 게시글_단일_조회_응답을_검증한다(ExtractableResponse<Response> 응답, PostDetailResponse 예상_데이터) {
        PostDetailResponse postDetailResponse = 응답.as(PostDetailResponse.class);
        assertThat(postDetailResponse).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(예상_데이터);
    }

    public static ExtractableResponse<Response> 게시글_전체_조회_요청을_보낸다() {
        return given()
                .get("/posts")
                .then().log().all()
                .extract();
    }

    public static void 게시글_전체_조회_응답을_검증한다(ExtractableResponse<Response> 응답, List<PostSimpleResponse> 예상_데이터) {
        List<PostSimpleResponse> responses = 응답.as(new TypeRef<>() {
        });
        assertThat(responses).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(예상_데이터);
    }
}
