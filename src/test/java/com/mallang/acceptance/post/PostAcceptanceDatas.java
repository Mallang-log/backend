package com.mallang.acceptance.post;

import com.mallang.post.application.query.PostDetailResponse;
import com.mallang.post.application.query.PostSimpleResponse;
import com.mallang.post.presentation.request.CreatePostRequest;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class PostAcceptanceDatas {

    public static PostDetailResponse 예상_게시글_단일_조회_응답(
            Long 게시글_ID,
            String 작성자_닉네임,
            String 제목,
            String 내용
    ) {
        return PostDetailResponse.builder()
                .id(게시글_ID)
                .writerNickname(작성자_닉네임)
                .title(제목)
                .content(내용)
                .build();
    }

    public static PostSimpleResponse 예상_게시글_전체_조회_응답(
            Long 게시글_ID,
            String 작성자_닉네임,
            String 제목,
            String 내용
    ) {
        return PostSimpleResponse.builder()
                .id(게시글_ID)
                .writerNickname(작성자_닉네임)
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
