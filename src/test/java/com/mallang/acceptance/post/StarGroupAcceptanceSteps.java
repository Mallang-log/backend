package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.post.presentation.request.CreateStarGroupRequest;
import com.mallang.post.presentation.request.UpdateStarGroupHierarchyRequest;
import com.mallang.post.presentation.request.UpdateStarGroupNameRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class StarGroupAcceptanceSteps {

    public static ExtractableResponse<Response> 즐겨찾기_그룹_생성_요청(
            String 세션_ID,
            String 그룹_이름,
            Long 부모_그룹_ID,
            Long 이전_형제_ID,
            Long 다음_형제_ID
    ) {
        return given(세션_ID)
                .body(new CreateStarGroupRequest(
                        그룹_이름,
                        부모_그룹_ID,
                        이전_형제_ID,
                        다음_형제_ID
                ))
                .post("/star-groups")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 즐겨찾기_그룹_생성_요청(
            String 세션_ID,
            CreateStarGroupRequest request
    ) {
        return given(세션_ID)
                .body(request)
                .post("/star-groups")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 즐겨찾기_그룹_계층구조_수정_요청(
            String 세션_ID,
            Long 즐겨찾기_그룹_ID,
            Long 변경할_상위_즐겨찾기_그룹_ID,
            Long 이전_형제_즐겨찾기_그룹_ID,
            Long 다음_형제_즐겨찾기_그룹_ID
    ) {
        return given(세션_ID)
                .body(new UpdateStarGroupHierarchyRequest(
                        변경할_상위_즐겨찾기_그룹_ID,
                        이전_형제_즐겨찾기_그룹_ID,
                        다음_형제_즐겨찾기_그룹_ID
                ))
                .put("/star-groups/{id}/hierarchy", 즐겨찾기_그룹_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 즐겨찾기_그룹_이름_수정_요청(
            String 세션_ID,
            Long 즐겨찾기_그룹_ID,
            String 변경할_이름
    ) {
        return given(세션_ID)
                .body(new UpdateStarGroupNameRequest(변경할_이름))
                .put("/star-groups/{id}/name", 즐겨찾기_그룹_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 즐겨찾기_그룹_제거_요청(
            String 세션_ID,
            Long 즐겨찾기_그룹_ID
    ) {
        return given(세션_ID)
                .delete("/star-groups/{id}", 즐겨찾기_그룹_ID)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 회원의_즐겨찾기_그룹_목록_조회_요청(Long 회원_ID) {
        return given()
                .param("memberId", 회원_ID)
                .get("/star-groups")
                .then()
                .extract();
    }
}
