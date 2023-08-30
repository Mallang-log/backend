package com.mallang.acceptance.comment;

import com.mallang.comment.query.data.AuthenticatedWriterData;
import com.mallang.comment.query.data.CommentData;
import com.mallang.comment.query.data.CommentWriterData;
import com.mallang.comment.query.data.UnAuthenticatedWriterData;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class CommentAcceptanceDatas {

    public static final boolean 공개 = false;
    public static final boolean 비공개 = true;

    public static CommentData 예상_댓글_조회_데이터(
            Long 댓글_ID,
            String 내용,
            boolean 비밀_댓글_여부,
            CommentWriterData 댓글_작성자_정보
    ) {
        return CommentData.builder()
                .id(댓글_ID)
                .content(내용)
                .secret(비밀_댓글_여부)
                .commentWriterData(댓글_작성자_정보)
                .build();
    }

    public static AuthenticatedWriterData 예상_댓글_작성자_데이터(
            String 닉네임,
            String 프로필_사진_URL
    ) {
        return AuthenticatedWriterData.builder()
                .nickname(닉네임)
                .profileImageUrl(프로필_사진_URL)
                .build();
    }

    public static UnAuthenticatedWriterData 예상_댓글_작성자_데이터(String 별명) {
        return UnAuthenticatedWriterData.builder()
                .nickname(별명)
                .build();
    }

    public static List<CommentData> 전체_조회_항목들(
            CommentData... 항목들
    ) {
        return Arrays.asList(항목들);
    }
}
