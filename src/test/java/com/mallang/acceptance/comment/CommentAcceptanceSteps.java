package com.mallang.acceptance.comment;

import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.comment.presentation.request.WriteAnonymousCommentRequest;
import com.mallang.comment.presentation.request.WriteAuthenticatedCommentRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class CommentAcceptanceSteps {

    public static ExtractableResponse<Response> 댓글_작성_요청(
            String 세션_ID,
            Long 포스트_ID,
            String 내용,
            boolean 비밀_여부
    ) {
        return given(세션_ID)
                .body(new WriteAuthenticatedCommentRequest(포스트_ID, 내용, 비밀_여부))
                .post("/comments")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 익명_댓글_작성_요청(
            Long 포스트_ID,
            String 내용,
            String 이름,
            String 암호
    ) {
        return given()
                .body(new WriteAnonymousCommentRequest(포스트_ID, 내용, 이름, 암호))
                .post("/comments/anonymous")
                .then()
                .log().all()
                .extract();
    }
}
