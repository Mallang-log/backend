package com.mallang.category.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.category.application.command.CreateCategoryCommand;
import com.mallang.category.query.response.CategoryResponse;
import com.mallang.common.ServiceTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("카테고리 조회 서비스(CategoryQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CategoryQueryServiceTest extends ServiceTest {

    @Test
    void 특정_블로그의_카테고리를_전체_조회한다() {
        // given
        Long 동훈_ID = 회원을_저장한다("동훈");
        Long 동훈_블로그_Id = 블로그_개설(동훈_ID, "donghun");
        categoryService.create(new CreateCategoryCommand(동훈_ID, 동훈_블로그_Id, "Node", null));

        Long 말랑_ID = 회원을_저장한다("말랑");
        Long 말랑_블로그_Id = 블로그_개설(말랑_ID, "mallang");
        Long springId = categoryService.create(new CreateCategoryCommand(말랑_ID, 말랑_블로그_Id, "Spring", null));
        Long jpaId = categoryService.create(new CreateCategoryCommand(말랑_ID, 말랑_블로그_Id, "JPA", springId));
        Long n1Id = categoryService.create(new CreateCategoryCommand(말랑_ID, 말랑_블로그_Id, "N + 1", jpaId));
        Long securityId = categoryService.create(new CreateCategoryCommand(말랑_ID, 말랑_블로그_Id, "Security", springId));
        Long oAuthId = categoryService.create(new CreateCategoryCommand(말랑_ID, 말랑_블로그_Id, "OAuth", securityId));
        Long csrfId = categoryService.create(new CreateCategoryCommand(말랑_ID, 말랑_블로그_Id, "CSRF", securityId));
        Long algorithmId = categoryService.create(new CreateCategoryCommand(말랑_ID, 말랑_블로그_Id, "Algorithm", null));
        Long dfsId = categoryService.create(new CreateCategoryCommand(말랑_ID, 말랑_블로그_Id, "DFS", algorithmId));
        List<CategoryResponse> expected = List.of(
                new CategoryResponse(springId, "Spring", List.of(
                        new CategoryResponse(jpaId, "JPA", List.of(
                                new CategoryResponse(n1Id, "N + 1", List.of())
                        )),
                        new CategoryResponse(securityId, "Security", List.of(
                                new CategoryResponse(oAuthId, "OAuth", List.of()),
                                new CategoryResponse(csrfId, "CSRF", List.of())
                        ))
                )),
                new CategoryResponse(algorithmId, "Algorithm", List.of(
                        new CategoryResponse(dfsId, "DFS", List.of())
                ))
        );

        // when
        List<CategoryResponse> allByMemberId = categoryQueryService.findAllByBlogId(말랑_블로그_Id);

        // then
        assertThat(allByMemberId).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
