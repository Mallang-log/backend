package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.given;
import static com.mallang.post.presentation.support.PostPresentationConstant.POST_PASSWORD_COOKIE;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.query.data.PostDetailData;
import com.mallang.post.query.data.PostDetailData.CategoryDetailInfo;
import com.mallang.post.query.data.PostDetailData.TagDetailInfos;
import com.mallang.post.query.data.PostDetailData.WriterDetailInfo;
import com.mallang.post.query.data.PostSimpleData;
import com.mallang.post.query.data.PostSimpleData.CategorySimpleInfo;
import com.mallang.post.query.data.PostSimpleData.TagSimpleInfos;
import com.mallang.post.query.data.PostSimpleData.WriterSimpleInfo;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import jakarta.annotation.Nullable;
import java.util.Arrays;
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
            @Nullable String 비밀번호
    ) {
        return given(세션_ID)
                .cookie(POST_PASSWORD_COOKIE, 비밀번호)
                .get("/posts/{id}", 포스트_ID)
                .then().log().all()
                .extract();
    }

    public static void 포스트_내용_검증(Long 포스트_ID, PostDetailData 예상_데이터) {
        var 포스트_조회_응답 = 포스트_단일_조회_요청(null, 포스트_ID, null);
        포스트_단일_조회_응답을_검증한다(포스트_조회_응답, 예상_데이터);
    }

    public static void 포스트_단일_조회_응답을_검증한다(ExtractableResponse<Response> 응답, PostDetailData 예상_데이터) {
        PostDetailData postDetailData = 응답.as(PostDetailData.class);
        assertThat(postDetailData).usingRecursiveComparison()
                .ignoringFields(
                        "writerInfo.writerId",
                        "writerInfo.writerProfileImageUrl",
                        "createdDate"
                )
                .isEqualTo(예상_데이터);
        assertThat(postDetailData.password()).isNull();
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
                .queryParam("content", 내용)
                .queryParam("titleOrContent", 제목_또는_내용)
                .get("/posts")
                .then().log().all()
                .extract();
    }

    public static void 포스트_전체_조회_응답을_검증한다(ExtractableResponse<Response> 응답, List<PostSimpleData> 예상_데이터) {
        List<PostSimpleData> responses = 응답.as(new TypeRef<>() {
        });
        assertThat(responses).usingRecursiveComparison()
                .ignoringFields("writerInfo.writerId", "writerInfo.writerProfileImageUrl", "createdDate")
                .isEqualTo(예상_데이터);
    }

    public static PostDetailData 포스트_단일_조회_데이터(
            Long 포스트_ID,
            String 작성자_닉네임,
            Long 카테고리_ID,
            String 카테고리_이름,
            String 제목,
            String 내용,
            String 썸네일_이미지_이름,
            Visibility 공개_범위,
            boolean 보호_여부,
            boolean 좋아요_클릭_여부,
            int 좋아요_수,
            String... 태그들
    ) {
        return PostDetailData.builder()
                .id(포스트_ID)
                .writerInfo(new WriterDetailInfo(null, 작성자_닉네임, null))
                .categoryInfo(new CategoryDetailInfo(카테고리_ID, 카테고리_이름))
                .tagDetailInfos(new TagDetailInfos(Arrays.asList(태그들)))
                .isLiked(좋아요_클릭_여부)
                .title(제목)
                .postThumbnailImageName(썸네일_이미지_이름)
                .visibility(공개_범위)
                .isProtected(보호_여부)
                .content(내용)
                .likeCount(좋아요_수)
                .build();
    }

    public static PostSimpleData 포스트_전체_조회_데이터(
            Long 포스트_ID,
            String 작성자_닉네임,
            Long 카테고리_ID,
            String 카테고리_이름,
            String 제목,
            String 내용,
            String 썸네일_이미지_이름,
            String 인트로,
            Visibility 공개_범위,
            String... 태그들
    ) {
        return PostSimpleData.builder()
                .id(포스트_ID)
                .writerInfo(new WriterSimpleInfo(null, 작성자_닉네임, null))
                .categoryInfo(new CategorySimpleInfo(카테고리_ID, 카테고리_이름))
                .tagSimpleInfos(new TagSimpleInfos(Arrays.asList(태그들)))
                .title(제목)
                .content(내용)
                .intro(인트로)
                .postThumbnailImageName(썸네일_이미지_이름)
                .visibility(공개_범위)
                .build();
    }

}
