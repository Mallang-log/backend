package com.mallang.acceptance.comment;

import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.comment.presentation.request.DeleteAnonymousCommentRequest;
import com.mallang.comment.presentation.request.UpdateAnonymousCommentRequest;
import com.mallang.comment.presentation.request.UpdateAuthenticatedCommentRequest;
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

    public static ExtractableResponse<Response> 비인증_댓글_작성_요청(
            Long 포스트_ID,
            String 내용,
            String 이름,
            String 암호
    ) {
        return given()
                .body(new WriteAnonymousCommentRequest(포스트_ID, 내용, 이름, 암호))
                .queryParam("unauthenticated", true)
                .post("/comments")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 댓글_수정_요청(String 세션_ID, Long 댓글_ID, String 수정_내용) {

        return given(세션_ID)
                .body(new UpdateAuthenticatedCommentRequest(수정_내용, false))
                .put("/comments/{id}", 댓글_ID)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 댓글_수정_요청(Long 댓글_ID, String 암호, String 수정_내용) {
        return given()
                .body(new UpdateAnonymousCommentRequest(수정_내용, 암호))
                .queryParam("unauthenticated", true)
                .put("/comments/{id}", 댓글_ID)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 댓글_삭제_요청(String 세션_ID, Long 댓글_ID) {
        return given(세션_ID)
                .delete("/comments/{id}", 댓글_ID)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 댓글_삭제_요청(Long 댓글_ID, String 암호) {
        return given()
                .queryParam("unauthenticated", "true")
                .body(new DeleteAnonymousCommentRequest(암호))
                .delete("/comments/{id}", 댓글_ID)
                .then().log().all()
                .extract();
    }
}
