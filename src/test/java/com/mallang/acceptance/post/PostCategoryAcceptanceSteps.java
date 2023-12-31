package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.post.presentation.request.CreatePostCategoryRequest;
import com.mallang.post.presentation.request.UpdatePostCategoryHierarchyRequest;
import com.mallang.post.presentation.request.UpdatePostCategoryNameRequest;
import com.mallang.post.query.response.PostCategoryResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class PostCategoryAcceptanceSteps {

    public static Long 카테고리_생성(
            String 세션_ID,
            String 블로그_이름,
            String 카테고리_이름,
            Long 부모_카테고리_ID,
            Long 이전_형제_ID,
            Long 다음_형제_ID
    ) {
        return ID를_추출한다(카테고리_생성_요청(세션_ID, 블로그_이름, 카테고리_이름, 부모_카테고리_ID, 이전_형제_ID, 다음_형제_ID));
    }

    public static Long 카테고리_생성(
            String 세션_ID,
            CreatePostCategoryRequest request
    ) {
        return ID를_추출한다(카테고리_생성_요청(세션_ID, request));
    }

    public static ExtractableResponse<Response> 카테고리_생성_요청(
            String 세션_ID,
            String 블로그_이름,
            String 카테고리_이름,
            Long 부모_카테고리_ID,
            Long 이전_형제_ID,
            Long 다음_형제_ID
    ) {
        return given(세션_ID)
                .body(new CreatePostCategoryRequest(
                        블로그_이름,
                        카테고리_이름,
                        부모_카테고리_ID,
                        이전_형제_ID,
                        다음_형제_ID
                ))
                .post("/categories")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 카테고리_생성_요청(
            String 세션_ID,
            CreatePostCategoryRequest request
    ) {
        return given(세션_ID)
                .body(request)
                .post("/categories")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 카테고리_계층구조_수정_요청(
            String 세션_ID,
            Long 카테고리_ID,
            Long 변경할_상위_카테고리_ID,
            Long 이전_형제_카테고리_ID,
            Long 다음_형제_카테고리_ID
    ) {
        return given(세션_ID)
                .body(new UpdatePostCategoryHierarchyRequest(
                        변경할_상위_카테고리_ID,
                        이전_형제_카테고리_ID,
                        다음_형제_카테고리_ID
                ))
                .put("/categories/{id}/hierarchy", 카테고리_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 카테고리_이름_수정_요청(
            String 세션_ID,
            Long 카테고리_ID,
            String 변경할_이름
    ) {
        return given(세션_ID)
                .body(new UpdatePostCategoryNameRequest(변경할_이름))
                .put("/categories/{id}/name", 카테고리_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 카테고리_제거_요청(
            String 세션_ID,
            Long 카테고리_ID
    ) {
        return given(세션_ID)
                .delete("/categories/{id}", 카테고리_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 블로그의_카테고리_조회_요청(String 블로그_이름) {
        return given()
                .param("blogName", 블로그_이름)
                .get("/categories")
                .then()
                .extract();
    }

    public static void 카테고리_조회_응답을_검증한다(ExtractableResponse<Response> 응답, List<PostCategoryResponse> 예상_응답) {
        List<PostCategoryResponse> actual = 응답.as(new TypeRef<>() {
        });
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(예상_응답);
    }
}
