package com.mallang.acceptance.post;

import com.mallang.post.application.query.PostDetailResponse;
import com.mallang.post.application.query.PostDetailResponse.CategoryDetailInfo;
import com.mallang.post.application.query.PostDetailResponse.TagDetailInfos;
import com.mallang.post.application.query.PostDetailResponse.WriterDetailInfo;
import com.mallang.post.application.query.PostSimpleResponse;
import com.mallang.post.application.query.PostSimpleResponse.CategorySimpleInfo;
import com.mallang.post.application.query.PostSimpleResponse.TagSimpleInfos;
import com.mallang.post.application.query.PostSimpleResponse.WriterSimpleInfo;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class PostAcceptanceDatas {

    public static PostDetailResponse 예상_포스트_단일_조회_응답(
            Long 포스트_ID,
            String 작성자_닉네임,
            Long 카테고리_ID,
            String 카테고리_이름,
            String 제목,
            String 내용,
            String... 태그들
    ) {
        return PostDetailResponse.builder()
                .id(포스트_ID)
                .writerInfo(new WriterDetailInfo(null, 작성자_닉네임, null))
                .categoryInfo(new CategoryDetailInfo(카테고리_ID, 카테고리_이름))
                .tagDetailInfos(new TagDetailInfos(Arrays.asList(태그들)))
                .title(제목)
                .content(내용)
                .build();
    }

    public static PostSimpleResponse 예상_포스트_전체_조회_응답(
            Long 포스트_ID,
            String 작성자_닉네임,
            Long 카테고리_ID,
            String 카테고리_이름,
            String 제목,
            String 내용,
            String... 태그들
    ) {
        return PostSimpleResponse.builder()
                .id(포스트_ID)
                .writerInfo(new WriterSimpleInfo(null, 작성자_닉네임, null))
                .categoryInfo(new CategorySimpleInfo(카테고리_ID, 카테고리_이름))
                .tagSimpleInfos(new TagSimpleInfos(Arrays.asList(태그들)))
                .title(제목)
                .content(내용)
                .build();
    }

    public static List<PostSimpleResponse> 전체_조회_항목들(
            PostSimpleResponse... 항목들
    ) {
        return Arrays.asList(항목들);
    }
}
