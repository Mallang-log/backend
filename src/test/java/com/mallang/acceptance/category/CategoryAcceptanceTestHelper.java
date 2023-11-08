package com.mallang.acceptance.category;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.given;

import com.mallang.category.presentation.request.CreateCategoryRequest;

@SuppressWarnings("NonAsciiCharacters")
public class CategoryAcceptanceTestHelper {

    public static Long 카테고리_생성(
            String 세션_ID,
            Long 블로그_ID,
            String 카테고리_이름,
            Long 부모_카테고리_ID
    ) {
        return ID를_추출한다(given(세션_ID)
                .body(new CreateCategoryRequest(블로그_ID, 카테고리_이름, 부모_카테고리_ID))
                .post("/categories")
                .then().log().all()
                .extract());
    }
}
