package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.given;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.presentation.request.CreatePostRequest;
import com.mallang.post.presentation.request.DeletePostRequest;
import com.mallang.post.presentation.request.UpdatePostRequest;
import com.mallang.post.query.data.PostManageDetailData;
import com.mallang.post.query.data.PostManageSimpleData;
import com.mallang.post.query.data.PostManageSimpleData.CategoryManageSimpleInfo;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class PostManageAcceptanceSteps {

    public static boolean 좋아요_눌림 = true;
    public static boolean 보호됨 = true;
    public static boolean 좋아요_안눌림 = false;
    public static boolean 보호되지_않음 = false;

    public static CreatePostRequest 공개_포스트_생성_데이터(String 블로그_이름) {
        return new CreatePostRequest(
                블로그_이름,
                "제목",
                "내용",
                "섬네일",
                "포스트 인트로 입니다.",
                PUBLIC,
                없음(),
                없음(),
                Collections.emptyList());
    }

    public static CreatePostRequest 보호_포스트_생성_데이터(String 블로그_이름) {
        return new CreatePostRequest(
                블로그_이름,
                "제목",
                "내용",
                "섬네일",
                "포스트 인트로 입니다.",
                PROTECTED,
                "1234",
                없음(),
                Collections.emptyList());
    }

    public static CreatePostRequest 비공개_포스트_생성_데이터(String 블로그_이름) {
        return new CreatePostRequest(
                블로그_이름,
                "제목",
                "내용",
                "섬네일",
                "포스트 인트로 입니다.",
                PRIVATE,
                없음(),
                없음(),
                Collections.emptyList());
    }

    public static Long 포스트_생성(
            String 세션_ID,
            String 블로그_이름,
            String 포스트_제목,
            String 포스트_내용,
            String 썸네일_이미지_이름,
            String 포스트_인트로,
            Visibility 공개_범위,
            String 비밀번호,
            Long 카테고리_ID,
            String... 태그들
    ) {
        return 포스트_생성(세션_ID, new CreatePostRequest(
                블로그_이름,
                포스트_제목,
                포스트_내용,
                썸네일_이미지_이름,
                포스트_인트로,
                공개_범위,
                비밀번호,
                카테고리_ID,
                Arrays.asList(태그들)));
    }

    public static Long 포스트_생성(
            String 세션_ID,
            CreatePostRequest 요청
    ) {
        return ID를_추출한다(포스트_생성_요청(세션_ID, 요청));
    }

    public static ExtractableResponse<Response> 포스트_생성_요청(
            String 세션_ID,
            String 블로그_이름,
            String 포스트_제목,
            String 포스트_내용,
            String 썸네일_이미지_이름,
            String 포스트_인트로,
            Visibility 공개_범위,
            String 비밀번호,
            Long 카테고리_ID,
            String... 태그들
    ) {
        CreatePostRequest request = new CreatePostRequest(
                블로그_이름,
                포스트_제목,
                포스트_내용,
                썸네일_이미지_이름,
                포스트_인트로,
                공개_범위,
                비밀번호,
                카테고리_ID,
                Arrays.asList(태그들)
        );
        return 포스트_생성_요청(세션_ID, request);
    }

    public static ExtractableResponse<Response> 포스트_생성_요청(
            String 세션_ID,
            CreatePostRequest 요청
    ) {
        return given(세션_ID)
                .body(요청)
                .when()
                .post("/manage/posts")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 포스트_수정_요청(
            String 세션_ID,
            Long 포스트_ID,
            String 업데이트_제목,
            String 업데이트_내용,
            String 썸네일_이미지_이름,
            String 업데이트_인트로,
            Visibility 공개_범위,
            String 비밀번호,
            Long 변경할_카테고리_ID,
            String... 태그들
    ) {
        return given(세션_ID)
                .body(new UpdatePostRequest(
                        업데이트_제목,
                        업데이트_내용,
                        썸네일_이미지_이름,
                        업데이트_인트로,
                        공개_범위,
                        비밀번호,
                        변경할_카테고리_ID,
                        Arrays.asList(태그들)))
                .put("/manage/posts/{id}", 포스트_ID)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 포스트_삭제_요청(String 말랑_세션_ID, Long 포스트_ID) {
        return given(말랑_세션_ID)
                .body(new DeletePostRequest(Arrays.asList(포스트_ID)))
                .delete("/manage/posts")
                .then()
                .log().all()
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
                .queryParam("blogName", 블로그_이름)
                .queryParam("categoryId", 카테고리_ID)
                .queryParam("title", 제목)
                .queryParam("content", 내용)
                .queryParam("visibility", 공개여부)
                .get("/manage/posts")
                .then().log().all()
                .extract();
    }

    public static PostManageSimpleData 내_글_관리_전체_조회_데이터(
            Long 포스트_ID,
            Long 카테고리_ID,
            String 카테고리_이름,
            String 제목,
            Visibility 공개_범위,
            String 비밀번호
    ) {
        return PostManageSimpleData.builder()
                .id(포스트_ID)
                .categoryInfo(new CategoryManageSimpleInfo(카테고리_ID, 카테고리_이름))
                .title(제목)
                .visibility(공개_범위)
                .password(비밀번호)
                .build();
    }

    public static void 내_관리_글_전체_조회_응답을_검증한다(ExtractableResponse<Response> 응답, List<PostManageSimpleData> 예상_데이터) {
        List<PostManageSimpleData> responses = 응답.as(new TypeRef<>() {
        });
        assertThat(responses).usingRecursiveComparison()
                .ignoringFields("createdDate")
                .isEqualTo(예상_데이터);
    }

    public static ExtractableResponse<Response> 내_관리_글_단일_조회_요청(String 세션_ID, Long 포스트_ID) {
        return given(세션_ID)
                .get("/manage/posts/{id}", 포스트_ID)
                .then()
                .log().all()
                .extract();
    }

    public static void 내_관리_글_단일_조회_응답을_검증한다(
            ExtractableResponse<Response> 응답,
            PostManageDetailData postManageDetailData
    ) {
        PostManageDetailData actual = 응답.as(PostManageDetailData.class);
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("createdDate")
                .isEqualTo(postManageDetailData);
    }
}
