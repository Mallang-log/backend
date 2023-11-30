package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.given;
import static com.mallang.post.presentation.support.PostPresentationConstant.POST_PASSWORD_COOKIE;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.common.presentation.PageResponse;
import com.mallang.post.query.response.PostDetailResponse;
import com.mallang.post.query.response.PostSearchResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import jakarta.annotation.Nullable;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class PostAcceptanceSteps {

    public static boolean 좋아요_눌림 = true;
    public static boolean 보호됨 = true;
    public static boolean 좋아요_안눌림 = false;
    public static boolean 보호되지_않음 = false;

    public static ExtractableResponse<Response> 포스트_단일_조회_요청(
            @Nullable String 세션_ID,
            Long 포스트_ID,
            String 블로그_이름,
            @Nullable String 비밀번호
    ) {
        return given(세션_ID)
                .cookie(POST_PASSWORD_COOKIE, 비밀번호)
                .get("/posts/{blogName}/{id}", 블로그_이름, 포스트_ID)
                .then().log().all()
                .extract();
    }

    public static void 포스트_단일_조회_응답을_검증한다(ExtractableResponse<Response> 응답, PostDetailResponse 예상_데이터) {
        PostDetailResponse postDetailResponse = 응답.as(PostDetailResponse.class);
        assertThat(postDetailResponse).usingRecursiveComparison()
                .ignoringFields(
                        "writer.writerId",
                        "writer.writerProfileImageUrl",
                        "createdDate",
                        "blogId"
                )
                .isEqualTo(예상_데이터);
        assertThat(postDetailResponse.password()).isNull();
    }

    public static ExtractableResponse<Response> 포스트_전체_조회_요청(
            Long 카테고리_ID,
            String 블로그_이름,
            String 태그,
            Long 작성자_ID,
            String 제목,
            String 내용,
            String 제목_또는_내용
    ) {
        return 포스트_전체_조회_요청(null, 카테고리_ID, 블로그_이름, 태그, 작성자_ID, 제목, 내용, 제목_또는_내용);
    }

    public static ExtractableResponse<Response> 포스트_전체_조회_요청(
            String 세션_ID,
            Long 카테고리_ID,
            String 블로그_이름,
            String 태그,
            Long 작성자_ID,
            String 제목,
            String 내용,
            String 제목_또는_내용
    ) {
        return given(세션_ID)
                .queryParam("categoryId", 카테고리_ID)
                .queryParam("blogName", 블로그_이름)
                .queryParam("tag", 태그)
                .queryParam("writerId", 작성자_ID)
                .queryParam("title", 제목)
                .queryParam("bodyText", 내용)
                .queryParam("titleOrBodyText", 제목_또는_내용)
                .get("/posts")
                .then().log().all()
                .extract();
    }

    public static void 포스트_전체_조회_응답을_검증한다(ExtractableResponse<Response> 응답, List<PostSearchResponse> 예상_데이터) {
        PageResponse<PostSearchResponse> responses = 응답.as(new TypeRef<>() {
        });
        assertThat(responses.content())
                .usingRecursiveComparison()
                .ignoringFields("writer.writerId", "writer.writerProfileImageUrl", "createdDate")
                .isEqualTo(예상_데이터);
    }
}
