package com.mallang.category.application;

import com.mallang.category.application.command.CreateCategoryCommand;
import com.mallang.category.domain.Category;
import com.mallang.category.domain.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@ActiveProfiles("test")
@Component
public class CategoryServiceTestHelper {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    public Long 최상위_카테고리를_저장한다(Long 회원_ID, String 블로그_이름, String 이름) {
        return 하위_카테고리를_저장한다(회원_ID, 블로그_이름, 이름, null);
    }

    public Long 하위_카테고리를_저장한다(Long 회원_ID, String 블로그_ID, String 이름, Long 부모_카테고리_ID) {
        return categoryService.create(new CreateCategoryCommand(회원_ID, 블로그_ID, 이름, 부모_카테고리_ID));
    }

    public Category 카테고리를_조회한다(Long 카테고리_ID) {
        return categoryRepository.getById(카테고리_ID);
    }
}
