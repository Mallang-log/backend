package com.mallang.acceptance.post;

import com.mallang.post.query.data.PostDetailData;
import com.mallang.post.query.data.PostDetailData.CategoryDetailInfo;
import com.mallang.post.query.data.PostDetailData.TagDetailInfos;
import com.mallang.post.query.data.PostDetailData.WriterDetailInfo;
import com.mallang.post.query.data.PostSimpleData;
import com.mallang.post.query.data.PostSimpleData.CategorySimpleInfo;
import com.mallang.post.query.data.PostSimpleData.TagSimpleInfos;
import com.mallang.post.query.data.PostSimpleData.WriterSimpleInfo;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class PostAcceptanceDatas {

    public static PostDetailData 예상_포스트_단일_조회_응답(
            Long 포스트_ID,
            String 작성자_닉네임,
            Long 카테고리_ID,
            String 카테고리_이름,
            String 제목,
            String 내용,
            String... 태그들
    ) {
        return PostDetailData.builder()
                .id(포스트_ID)
                .writerInfo(new WriterDetailInfo(null, 작성자_닉네임, null))
                .categoryInfo(new CategoryDetailInfo(카테고리_ID, 카테고리_이름))
                .tagDetailInfos(new TagDetailInfos(Arrays.asList(태그들)))
                .title(제목)
                .content(내용)
                .build();
    }

    public static PostSimpleData 예상_포스트_전체_조회_응답(
            Long 포스트_ID,
            String 작성자_닉네임,
            Long 카테고리_ID,
            String 카테고리_이름,
            String 제목,
            String 내용,
            String... 태그들
    ) {
        return PostSimpleData.builder()
                .id(포스트_ID)
                .writerInfo(new WriterSimpleInfo(null, 작성자_닉네임, null))
                .categoryInfo(new CategorySimpleInfo(카테고리_ID, 카테고리_이름))
                .tagSimpleInfos(new TagSimpleInfos(Arrays.asList(태그들)))
                .title(제목)
                .content(내용)
                .build();
    }

    public static List<PostSimpleData> 전체_조회_항목들(
            PostSimpleData... 항목들
    ) {
        return Arrays.asList(항목들);
    }
}
