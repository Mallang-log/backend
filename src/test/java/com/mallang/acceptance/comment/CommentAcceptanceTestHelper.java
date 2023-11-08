package com.mallang.acceptance.comment;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.댓글_작성_요청;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.비인증_댓글_작성_요청;

@SuppressWarnings("NonAsciiCharacters")
public class CommentAcceptanceTestHelper {

    public static Long 댓글_작성(
            String 세션_ID,
            Long 포스트_ID,
            String 내용,
            boolean 비밀_여부
    ) {
        return 댓글_작성(세션_ID, 포스트_ID, 내용, 비밀_여부, null);
    }

    public static Long 댓글_작성(
            String 세션_ID,
            Long 포스트_ID,
            String 내용,
            boolean 비밀_여부,
            Long 부모_댓글_Id
    ) {
        return ID를_추출한다(댓글_작성_요청(
                세션_ID,
                포스트_ID,
                내용,
                비밀_여부,
                부모_댓글_Id
        ));
    }

    public static Long 비인증_댓글_작성(
            Long 포스트_ID,
            String 내용,
            String 이름,
            String 암호
    ) {
        return ID를_추출한다(비인증_댓글_작성_요청(
                포스트_ID,
                내용,
                이름,
                암호
        ));
    }

    public static Long 비인증_댓글_작성(
            Long 포스트_ID,
            String 내용,
            String 이름,
            String 암호,
            Long 부모_댓글_Id
    ) {
        return ID를_추출한다(비인증_댓글_작성_요청(
                포스트_ID,
                내용,
                이름,
                암호,
                부모_댓글_Id
        ));
    }
}
