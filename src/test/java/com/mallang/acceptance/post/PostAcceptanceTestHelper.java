package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.given;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_요청;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_응답을_검증한다;

import com.mallang.post.presentation.request.CreatePostRequest;
import com.mallang.post.query.data.PostDetailData;
import java.util.Arrays;

@SuppressWarnings("NonAsciiCharacters")
public class PostAcceptanceTestHelper {

    public static Long 포스트_생성(
            String 세션_ID,
            String 블로그_이름,
            String 포스트_제목,
            String 포스트_내용,
            Long 카테고리_ID,
            String... 태그들
    ) {
        return ID를_추출한다(given(세션_ID)
                .body(new CreatePostRequest(블로그_이름, 포스트_제목, 포스트_내용, 카테고리_ID, Arrays.asList(태그들)))
                .when()
                .post("/posts")
                .then().log().all()
                .extract());
    }

    public static void 포스트_내용_검증(String 블로그_이름, Long 포스트_ID, PostDetailData 예상_데이터) {
        var 포스트_조회_응답 = 포스트_단일_조회_요청(블로그_이름, 포스트_ID);
        포스트_단일_조회_응답을_검증한다(포스트_조회_응답, 예상_데이터);
    }
}
