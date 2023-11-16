package com.mallang.acceptance.category;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.category.presentation.request.CreateCategoryRequest;
import com.mallang.category.presentation.request.UpdateCategoryRequest;
import com.mallang.category.query.data.CategoryData;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class CategoryAcceptanceSteps {

    public static Long 카테고리_생성(
            String 세션_ID,
            Long 블로그_ID,
            String 카테고리_이름,
            Long 부모_카테고리_ID
    ) {
        return ID를_추출한다(카테고리_생성_요청(세션_ID, 블로그_ID, 카테고리_이름, 부모_카테고리_ID));
    }

    public static ExtractableResponse<Response> 카테고리_생성_요청(
            String 세션_ID,
            Long 블로그_ID,
            String 카테고리_이름,
            Long 부모_카테고리_ID
    ) {
        return given(세션_ID)
                .body(new CreateCategoryRequest(블로그_ID, 카테고리_이름, 부모_카테고리_ID))
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

    public static ExtractableResponse<Response> 카테고리_제거_요청(
            String 세션_ID,
            Long 카테고리_ID
    ) {
        return given(세션_ID)
                .delete("/categories/{id}", 카테고리_ID)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 블로그의_카테고리_조회_요청(Long 블로그_ID) {
        return given()
                .param("blogId", 블로그_ID)
                .get("/categories")
                .then().log().all()
                .extract();
    }

    public static CategoryData 카테고리_조회_응답_데이터(
            Long 카테고리_ID,
            String 이름,
            List<CategoryData> 하위_카테고리들
    ) {
        return CategoryData.builder()
                .id(카테고리_ID)
                .name(이름)
                .children(하위_카테고리들)
                .build();
    }

    public static List<CategoryData> 하위_카테고리들(
            CategoryData... 하위_카테고리들
    ) {
        return Arrays.asList(하위_카테고리들);
    }

    public static void 카테고리_조회_응답을_검증한다(ExtractableResponse<Response> 응답, List<CategoryData> 예상_응답) {
        List<CategoryData> actual = 응답.as(new TypeRef<>() {
        });
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(예상_응답);
    }
}
