package com.mallang.acceptance.category;

import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.category.presentation.request.CreateCategoryRequest;
import com.mallang.category.presentation.request.UpdateCategoryRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class CategoryAcceptanceSteps {

    public static ExtractableResponse<Response> 카테고리_생성_요청(
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

    public static ExtractableResponse<Response> 카테고리_수정_요청(
            String 세션_ID,
            Long 카테고리_ID,
            String 변경할_이름,
            Long 변경할_상위_카테고리_ID
    ) {
        return given(세션_ID)
                .body(new UpdateCategoryRequest(변경할_이름, 변경할_상위_카테고리_ID))
                .put("/categories/{id}", 카테고리_ID)
                .then().log().all()
                .extract();
    }
}
