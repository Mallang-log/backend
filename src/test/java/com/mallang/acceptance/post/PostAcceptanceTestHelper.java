package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.post.presentation.request.CreatePostRequest;

@SuppressWarnings("NonAsciiCharacters")
public class PostAcceptanceTestHelper {

    public static Long 포스트_생성(
            String 세션_ID,
            String 포스트_제목,
            String 포스트_내용,
            Long 카테고리_ID
    ) {
        return ID를_추출한다(given(세션_ID)
                .body(new CreatePostRequest(포스트_제목, 포스트_내용, 카테고리_ID))
                .when()
                .post("/posts")
                .then().log().all()
                .extract());
    }
}
