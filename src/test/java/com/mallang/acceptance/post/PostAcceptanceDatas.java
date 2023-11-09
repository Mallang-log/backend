package com.mallang.acceptance.post;

import com.mallang.post.domain.visibility.PostVisibility.Visibility;
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

    public static boolean 좋아요_눌림 = true;
    public static boolean 좋아요_안눌림 = false;

    public static PostDetailData 예상_포스트_단일_조회_응답(
            Long 포스트_ID,
            String 작성자_닉네임,
            Long 카테고리_ID,
            String 카테고리_이름,
            String 제목,
            String 내용,
            Visibility 공개_범위,
            boolean 좋아요_클릭_여부,
            String... 태그들
    ) {
        return 예상_포스트_단일_조회_응답(포스트_ID, 작성자_닉네임, 카테고리_ID, 카테고리_이름, 제목, 내용, 공개_범위, 좋아요_클릭_여부, 0, 태그들);
    }

    public static PostDetailData 예상_포스트_단일_조회_응답(
            Long 포스트_ID,
            String 작성자_닉네임,
            Long 카테고리_ID,
            String 카테고리_이름,
            String 제목,
            String 내용,
            Visibility 공개_범위,
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
                .visibility(공개_범위)
                .content(내용)
                .likeCount(좋아요_수)
                .build();
    }

    public static PostSimpleData 예상_포스트_전체_조회_응답(
            Long 포스트_ID,
            String 작성자_닉네임,
            Long 카테고리_ID,
            String 카테고리_이름,
            String 제목,
            String 내용,
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
                .visibility(공개_범위)
                .build();
    }

    public static List<PostSimpleData> 전체_조회_항목들(
            PostSimpleData... 항목들
    ) {
        return Arrays.asList(항목들);
    }
}
