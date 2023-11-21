package com.mallang.category.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.auth.MemberServiceTestHelper;
import com.mallang.blog.application.BlogServiceTestHelper;
import com.mallang.category.application.CategoryServiceTestHelper;
import com.mallang.category.query.response.CategoryResponse;
import com.mallang.common.ServiceTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("카테고리 조회 서비스(CategoryQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ServiceTest
class CategoryQueryServiceTest {

    @Autowired
    private BlogServiceTestHelper blogServiceTestHelper;

    @Autowired
    private CategoryQueryService categoryQueryService;

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private CategoryServiceTestHelper categoryServiceTestHelper;

    @Test
    void 특정_블로그의_카테고리를_전체_조회한다() {
        // given
        Long 동훈_ID = memberServiceTestHelper.회원을_저장한다("동훈");
        String 동훈_블로그_이름 = blogServiceTestHelper.블로그_개설(동훈_ID, "donghun").getName();
        categoryServiceTestHelper.최상위_카테고리를_저장한다(동훈_ID, 동훈_블로그_이름, "Node");

        Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
        String 말랑_블로그_이름 = blogServiceTestHelper.블로그_개설(말랑_ID, "mallang").getName();
        Long springId = categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, 말랑_블로그_이름, "Spring");
        Long jpaId = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, 말랑_블로그_이름, "JPA", springId);
        Long n1Id = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, 말랑_블로그_이름, "N + 1", jpaId);
        Long securityId = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, 말랑_블로그_이름, "Security", springId);
        Long oAuthId = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, 말랑_블로그_이름, "OAuth", securityId);
        Long csrfId = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, 말랑_블로그_이름, "CSRF", securityId);
        Long algorithmId = categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, 말랑_블로그_이름, "Algorithm");
        Long dfsId = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, 말랑_블로그_이름, "DFS", algorithmId);
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
        List<CategoryResponse> allByMemberId = categoryQueryService.findAllByBlogName(말랑_블로그_이름);

        // then
        assertThat(allByMemberId).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
