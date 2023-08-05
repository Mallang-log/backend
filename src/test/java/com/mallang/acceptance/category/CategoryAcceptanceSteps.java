package com.mallang.acceptance.category;

import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.category.presentation.request.CreateCategoryRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class CategoryAcceptanceSteps {

    public static ExtractableResponse<Response> 카테고리_생성_요청을_보낸다(
            String 세션_ID,
            String 카테고리_이름,
            Long 부모_카테고리_ID
    ) {
        return given(세션_ID)
                .body(new CreateCategoryRequest(카테고리_이름, 부모_카테고리_ID))
                .post("/categories")
                .then().log().all()
                .extract();
    }
}
