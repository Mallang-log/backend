package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.post.application.query.PostDetailResponse;
import com.mallang.post.application.query.PostSimpleResponse;
import com.mallang.post.presentation.request.CreatePostRequest;
import com.mallang.post.presentation.request.UpdatePostRequest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class PostAcceptanceSteps {

    public static ExtractableResponse<Response> 포스트_생성_요청(
            String 세션_ID,
            String 포스트_제목,
            String 포스트_내용,
            Long 카테고리_ID
    ) {
        return given(세션_ID)
                .body(new CreatePostRequest(포스트_제목, 포스트_내용, 카테고리_ID))
                .when()
                .post("/posts")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 포스트_수정_요청(
            String 세션_ID,
            Long 포스트_ID,
            String 업데이트_제목,
            String 업데이트_내용,
            Long 변경할_카테고리_ID
    ) {
        return given(세션_ID)
                .body(new UpdatePostRequest(업데이트_제목, 업데이트_내용, 변경할_카테고리_ID))
                .put("/posts/{id}", 포스트_ID)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 포스트_단일_조회_요청(Long 포스트_ID) {
        return given()
                .get("/posts/{id}", 포스트_ID)
                .then().log().all()
                .extract();
    }

    public static void 포스트_단일_조회_응답을_검증한다(ExtractableResponse<Response> 응답, PostDetailResponse 예상_데이터) {
        PostDetailResponse postDetailResponse = 응답.as(PostDetailResponse.class);
        assertThat(postDetailResponse).usingRecursiveComparison()
                .ignoringFields("writerInfo.writerId", "writerInfo.writerProfileImageUrl", "createdDate")
                .isEqualTo(예상_데이터);
    }

    public static ExtractableResponse<Response> 포스트_전체_조회_요청(Long 카테고리_ID) {
        return given()
                .queryParam("categoryId", 카테고리_ID)
                .get("/posts")
                .then().log().all()
                .extract();
    }

    public static void 포스트_전체_조회_응답을_검증한다(ExtractableResponse<Response> 응답, List<PostSimpleResponse> 예상_데이터) {
        List<PostSimpleResponse> responses = 응답.as(new TypeRef<>() {
        });
        assertThat(responses).usingRecursiveComparison()
                .ignoringFields("writerInfo.writerId", "writerInfo.writerProfileImageUrl", "createdDate")
                .isEqualTo(예상_데이터);
    }
}
