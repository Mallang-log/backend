package com.mallang.acceptance.comment;

import com.mallang.comment.query.data.AuthenticatedCommentData;
import com.mallang.comment.query.data.CommentData;
import com.mallang.comment.query.data.UnAuthenticatedCommentData;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class CommentAcceptanceDatas {

    public static final boolean 공개 = false;
    public static final boolean 비공개 = true;
    public static final boolean 삭제됨 = true;
    public static final boolean 삭제되지_않음 = false;

    public static AuthenticatedCommentData 인증된_댓글_조회_데이터(
            Long 댓글_ID,
            String 내용,
            boolean 비밀_댓글_여부,
            AuthenticatedCommentData.WriterData 댓글_작성자_정보,
            CommentData... 대댓글들
    ) {
        return 인증된_댓글_조회_데이터(댓글_ID, 내용, 비밀_댓글_여부, 댓글_작성자_정보, false, 대댓글들);
    }

    public static AuthenticatedCommentData 인증된_댓글_조회_데이터(
            Long 댓글_ID,
            String 내용,
            boolean 비밀_댓글_여부,
            AuthenticatedCommentData.WriterData 댓글_작성자_정보,
            boolean 삭제_여부,
            CommentData... 대댓글들
    ) {
        return AuthenticatedCommentData.builder()
                .id(댓글_ID)
                .content(내용)
                .secret(비밀_댓글_여부)
                .writerData(댓글_작성자_정보)
                .deleted(삭제_여부)
                .children(Arrays.asList(대댓글들))
                .build();
    }

    public static UnAuthenticatedCommentData 비인증_댓글_조회_데이터(
            Long 댓글_ID,
            String 내용,
            UnAuthenticatedCommentData.WriterData 댓글_작성자_정보,
            CommentData... 대댓글들
    ) {
        return 비인증_댓글_조회_데이터(댓글_ID, 내용, 댓글_작성자_정보, false, 대댓글들);
    }

    public static UnAuthenticatedCommentData 비인증_댓글_조회_데이터(
            Long 댓글_ID,
            String 내용,
            UnAuthenticatedCommentData.WriterData 댓글_작성자_정보,
            boolean 삭제_여부,
            CommentData... 대댓글들
    ) {
        return UnAuthenticatedCommentData.builder()
                .id(댓글_ID)
                .content(내용)
                .writerData(댓글_작성자_정보)
                .deleted(삭제_여부)
                .children(Arrays.asList(대댓글들))
                .build();
    }


    public static AuthenticatedCommentData.WriterData 인증된_댓글_작성자_데이터(
            String 닉네임,
            String 프로필_사진_URL
    ) {
        return new AuthenticatedCommentData.WriterData(null, 닉네임, 프로필_사진_URL);
    }

    public static UnAuthenticatedCommentData.WriterData 비인증_댓글_작성자_데이터(String 별명) {
        return new UnAuthenticatedCommentData.WriterData(별명);
    }

    public static List<CommentData> 전체_조회_항목들(
            CommentData... 항목들
    ) {
        return Arrays.asList(항목들);
    }
}
