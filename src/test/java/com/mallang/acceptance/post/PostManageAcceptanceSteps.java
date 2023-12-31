package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.given;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.common.presentation.PageResponse;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import com.mallang.post.presentation.request.CreatePostFromDraftRequest;
import com.mallang.post.presentation.request.CreatePostRequest;
import com.mallang.post.presentation.request.DeletePostRequest;
import com.mallang.post.presentation.request.UpdatePostRequest;
import com.mallang.post.query.response.PostManageDetailResponse;
import com.mallang.post.query.response.PostManageSearchResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class PostManageAcceptanceSteps {

    public static CreatePostRequest 공개_포스트_생성_데이터(String 블로그_이름) {
        return new CreatePostRequest(
                블로그_이름,
                "제목",
                "포스트 인트로 입니다.", "내용",
                "섬네일",
                PUBLIC,
                없음(),
                없음(),
                Collections.emptyList());
    }

    public static CreatePostRequest 보호_포스트_생성_데이터(String 블로그_이름) {
        return new CreatePostRequest(
                블로그_이름,
                "제목",
                "포스트 인트로 입니다.", "내용",
                "섬네일",
                PROTECTED,
                "1234",
                없음(),
                Collections.emptyList());
    }

    public static CreatePostRequest 비공개_포스트_생성_데이터(String 블로그_이름) {
        return new CreatePostRequest(
                블로그_이름,
                "제목",
                "포스트 인트로 입니다.", "내용",
                "섬네일",
                PRIVATE,
                없음(),
                없음(),
                Collections.emptyList());
    }

    public static Long 포스트_생성(
            String 세션_ID,
            CreatePostRequest 요청
    ) {
        return ID를_추출한다(포스트_생성_요청(세션_ID, 요청));
    }

    public static ExtractableResponse<Response> 포스트_생성_요청(
            String 세션_ID,
            CreatePostRequest 요청
    ) {
        return given(세션_ID)
                .body(요청)
                .when()
                .post("/manage/posts")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 임시_글로부터_포스트_생성_요청(
            String 세션_ID,
            CreatePostFromDraftRequest 요청
    ) {
        return given(세션_ID)
                .body(요청)
                .when()
                .post("/manage/posts/from-draft")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 포스트_수정_요청(
            String 세션_ID,
            Long 포스트_ID,
            UpdatePostRequest request
    ) {
        return given(세션_ID)
                .body(request)
                .put("/manage/posts/{id}", 포스트_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 포스트_삭제_요청(String 말랑_세션_ID, Long 포스트_ID, String 블로그_이름) {
        return given(말랑_세션_ID)
                .body(new DeletePostRequest(Arrays.asList(포스트_ID), 블로그_이름))
                .delete("/manage/posts")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 내_관리_글_목록_조회_요청(
            String 세션_ID,
            String 블로그_이름,
            Long 카테고리_ID,
            String 제목,
            String 내용,
            Visibility 공개여부
    ) {
        return given(세션_ID)
                .queryParam("categoryId", 카테고리_ID)
                .queryParam("title", 제목)
                .queryParam("bodyText", 내용)
                .queryParam("visibility", 공개여부)
                .get("/manage/posts/{blogName}", 블로그_이름)
                .then()
                .extract();
    }

    public static void 내_관리_글_전체_조회_응답을_검증한다(ExtractableResponse<Response> 응답, List<PostManageSearchResponse> 예상_데이터) {
        PageResponse<PostManageSearchResponse> responses = 응답.as(new TypeRef<>() {
        });
        assertThat(responses.content())
                .usingRecursiveComparison()
                .ignoringFields("createdDate")
                .isEqualTo(예상_데이터);
    }

    public static ExtractableResponse<Response> 내_관리_글_단일_조회_요청(String 세션_ID, String 블로그_이름, Long 포스트_ID) {
        return given(세션_ID)
                .get("/manage/posts/{blogName}/{id}", 블로그_이름, 포스트_ID)
                .then()
                .extract();
    }

    public static void 내_관리_글_단일_조회_응답을_검증한다(
            ExtractableResponse<Response> 응답,
            PostManageDetailResponse postManageDetailResponse
    ) {
        PostManageDetailResponse actual = 응답.as(PostManageDetailResponse.class);
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("createdDate")
                .isEqualTo(postManageDetailResponse);
    }
}
