package com.mallang.acceptance.category;

import com.mallang.category.application.query.CategoryResponse;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class CategoryAcceptanceDatas {

    public static CategoryResponse 카테고리_조회_응답_데이터(
            Long 카테고리_ID,
            String 이름,
            List<CategoryResponse> 하위_카테고리들
    ) {
        return CategoryResponse.builder()
                .id(카테고리_ID)
                .name(이름)
                .children(하위_카테고리들)
                .build();
    }

    public static List<CategoryResponse> 하위_카테고리들(
            CategoryResponse... 하위_카테고리들
    ) {
        return Arrays.asList(하위_카테고리들);
    }

    public static List<CategoryResponse> 전체_조회_항목들(
            CategoryResponse... 항목들
    ) {
        return Arrays.asList(항목들);
    }
}
