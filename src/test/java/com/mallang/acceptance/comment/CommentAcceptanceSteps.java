package com.mallang.acceptance.comment;

import static com.mallang.acceptance.AcceptanceSteps.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.comment.presentation.request.DeleteAnonymousCommentRequest;
import com.mallang.comment.presentation.request.UpdateAnonymousCommentRequest;
import com.mallang.comment.presentation.request.UpdateAuthenticatedCommentRequest;
import com.mallang.comment.presentation.request.WriteAnonymousCommentRequest;
import com.mallang.comment.presentation.request.WriteAuthenticatedCommentRequest;
import com.mallang.comment.query.data.CommentData;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class CommentAcceptanceSteps {

    public static ExtractableResponse<Response> 댓글_작성_요청(
            String 세션_ID,
            Long 포스트_ID,
            String 내용,
            boolean 비밀_여부
    ) {
        return 댓글_작성_요청(세션_ID, 포스트_ID, 내용, 비밀_여부, null);
    }

    public static ExtractableResponse<Response> 댓글_작성_요청(
            String 세션_ID,
            Long 포스트_ID,
            String 내용,
            boolean 비밀_여부,
            Long 부모_댓글_ID
    ) {
        return given(세션_ID)
                .body(new WriteAuthenticatedCommentRequest(포스트_ID, 내용, 비밀_여부, 부모_댓글_ID))
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
        return 비인증_댓글_작성_요청(포스트_ID, 내용, 이름, 암호, null);
    }

    public static ExtractableResponse<Response> 비인증_댓글_작성_요청(
            Long 포스트_ID,
            String 내용,
            String 이름,
            String 암호,
            Long 부모_댓글_ID
    ) {
        return given()
                .body(new WriteAnonymousCommentRequest(포스트_ID, 내용, 이름, 암호, 부모_댓글_ID))
                .queryParam("unauthenticated", true)
                .post("/comments")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 댓글_수정_요청(String 세션_ID, Long 댓글_ID, String 수정_내용, boolean 비공개_여부) {

        return given(세션_ID)
                .body(new UpdateAuthenticatedCommentRequest(수정_내용, 비공개_여부))
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

    public static ExtractableResponse<Response> 특정_포스팅의_댓글_전체_조회(String 세션_ID, Long 포스트_ID) {
        return given(세션_ID)
                .queryParam("postId", 포스트_ID)
                .get("/comments")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 특정_포스팅의_댓글_전체_조회(Long 포스트_ID) {
        return given()
                .queryParam("postId", 포스트_ID)
                .get("/comments")
                .then().log().all()
                .extract();
    }

    public static void 특정_포스트의_댓글_전체_조회_응답을_검증한다(ExtractableResponse<Response> 응답, List<CommentData> 예상_데이터) {
        List<CommentData> responses = 응답.as(new TypeRef<>() {
        });
        assertThat(responses).usingRecursiveComparison()
                .ignoringFields(
                        "createdDate",
                        "commentWriterData.memberId",
                        "children.createdDate",
                        "children.commentWriterData.memberId"
                )
                .isEqualTo(예상_데이터);
    }
}
