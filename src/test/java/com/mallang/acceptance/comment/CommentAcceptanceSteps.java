package com.mallang.acceptance.comment;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.given;
import static com.mallang.post.presentation.support.PostPresentationConstant.POST_PASSWORD_COOKIE;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.comment.presentation.request.DeleteUnAuthCommentRequest;
import com.mallang.comment.presentation.request.UpdateAuthCommentRequest;
import com.mallang.comment.presentation.request.UpdateUnAuthCommentRequest;
import com.mallang.comment.presentation.request.WriteAuthCommentRequest;
import com.mallang.comment.presentation.request.WriteUnAuthCommentRequest;
import com.mallang.comment.query.response.CommentResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import jakarta.annotation.Nullable;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class CommentAcceptanceSteps {

    public static final boolean 공개 = false;
    public static final boolean 비공개 = true;
    public static final boolean 삭제됨 = true;
    public static final boolean 삭제되지_않음 = false;

    public static Long 댓글_작성(
            String 세션_ID,
            WriteAuthCommentRequest request,
            @Nullable String 포스트_비밀번호
    ) {
        return ID를_추출한다(댓글_작성_요청(세션_ID, request, 포스트_비밀번호));
    }

    public static ExtractableResponse<Response> 댓글_작성_요청(
            String 세션_ID,
            WriteAuthCommentRequest request,
            String 포스트_비밀번호
    ) {
        return given(세션_ID)
                .cookie(POST_PASSWORD_COOKIE, 포스트_비밀번호)
                .body(request)
                .post("/comments")
                .then()
                //.log().all()
                .extract();
    }

    public static Long 비인증_댓글_작성(
            WriteUnAuthCommentRequest request,
            @Nullable String 포스트_비밀번호
    ) {
        return ID를_추출한다(비인증_댓글_작성_요청(request, 포스트_비밀번호));
    }

    public static ExtractableResponse<Response> 비인증_댓글_작성_요청(
            WriteUnAuthCommentRequest request,
            String 포스트_비밀번호
    ) {
        return given()
                .cookie(POST_PASSWORD_COOKIE, 포스트_비밀번호)
                .body(request)
                .queryParam("unauthenticated", true)
                .post("/comments")
                .then()
                //.log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 댓글_수정_요청(
            String 세션_ID,
            Long 댓글_ID,
            UpdateAuthCommentRequest request,
            @Nullable String 포스트_비밀번호
    ) {
        return given(세션_ID)
                .cookie(POST_PASSWORD_COOKIE, 포스트_비밀번호)
                .body(request)
                .put("/comments/{id}", 댓글_ID)
                .then()
                //.log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 비인증_댓글_수정_요청(
            Long 댓글_ID,
            String 암호,
            String 수정_내용,
            @Nullable String 포스트_비밀번호
    ) {
        return given()
                .cookie(POST_PASSWORD_COOKIE, 포스트_비밀번호)
                .body(new UpdateUnAuthCommentRequest(수정_내용, 암호))
                .queryParam("unauthenticated", true)
                .put("/comments/{id}", 댓글_ID)
                .then()
                //.log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 댓글_삭제_요청(
            String 세션_ID,
            Long 댓글_ID,
            @Nullable String 포스트_비밀번호
    ) {
        return given(세션_ID)
                .cookie(POST_PASSWORD_COOKIE, 포스트_비밀번호)
                .delete("/comments/{id}", 댓글_ID)
                .then()
                //.log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 비인증_댓글_삭제_요청(
            Long 댓글_ID,
            String 암호,
            @Nullable String 포스트_비밀번호
    ) {
        return given()
                .cookie(POST_PASSWORD_COOKIE, 포스트_비밀번호)
                .queryParam("unauthenticated", true)
                .body(new DeleteUnAuthCommentRequest(암호))
                .delete("/comments/{id}", 댓글_ID)
                .then()
                //.log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 포스트_작성자의_비인증_댓글_삭제_요청(
            String 세션_ID,
            Long 댓글_ID,
            @Nullable String 포스트_비밀번호
    ) {
        return given(세션_ID)
                .cookie(POST_PASSWORD_COOKIE, 포스트_비밀번호)
                .queryParam("unauthenticated", true)
                .body(new DeleteUnAuthCommentRequest(null))
                .delete("/comments/{id}", 댓글_ID)
                .then()
                //.log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 특정_포스트의_댓글_전체_조회_요청(
            Long 포스트_ID,
            String 블로그_이름,
            @Nullable String 포스트_비밀번호
    ) {
        return 특정_포스트의_댓글_전체_조회_요청(null, 포스트_ID, 블로그_이름, 포스트_비밀번호);
    }

    public static ExtractableResponse<Response> 특정_포스트의_댓글_전체_조회_요청(
            String 세션_ID,
            Long 포스트_ID,
            String 블로그_이름,
            @Nullable String 포스트_비밀번호
    ) {
        return given(세션_ID)
                .cookie(POST_PASSWORD_COOKIE, 포스트_비밀번호)
                .queryParam("postId", 포스트_ID)
                .queryParam("blogName", 블로그_이름)
                .get("/comments")
                .then()
                //.log().all()
                .extract();
    }

    public static void 특정_포스트의_댓글_전체_조회_응답을_검증한다(ExtractableResponse<Response> 응답,
                                                 List<? extends CommentResponse> 예상_데이터) {
        List<CommentResponse> responses = 응답.as(new TypeRef<>() {
        });
        assertThat(responses).usingRecursiveComparison()
                .ignoringFields(
                        "createdDate",
                        "writer.memberId",
                        "children.createdDate",
                        "children.writer.memberId"
                )
                .isEqualTo(예상_데이터);
    }
}
