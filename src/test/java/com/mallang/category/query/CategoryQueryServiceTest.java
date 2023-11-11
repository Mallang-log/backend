package com.mallang.category.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.auth.MemberServiceTestHelper;
import com.mallang.blog.application.BlogServiceTestHelper;
import com.mallang.category.application.CategoryServiceTestHelper;
import com.mallang.category.query.data.CategoryData;
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
        Long 동훈_블로그_ID = blogServiceTestHelper.블로그_개설후_ID_반환(동훈_ID, "donghun");
        categoryServiceTestHelper.최상위_카테고리를_저장한다(동훈_ID, 동훈_블로그_ID, "Node");

        Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
        Long 말랑_블로그_ID = blogServiceTestHelper.블로그_개설후_ID_반환(말랑_ID, "mallang");
        Long springId = categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, 말랑_블로그_ID, "Spring");
        Long jpaId = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, 말랑_블로그_ID, "JPA", springId);
        Long n1Id = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, 말랑_블로그_ID, "N + 1", jpaId);
        Long securityId = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, 말랑_블로그_ID, "Security", springId);
        Long oAuthId = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, 말랑_블로그_ID, "OAuth", securityId);
        Long csrfId = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, 말랑_블로그_ID, "CSRF", securityId);
        Long algorithmId = categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, 말랑_블로그_ID, "Algorithm");
        Long dfsId = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, 말랑_블로그_ID, "DFS", algorithmId);
        List<CategoryData> expected = List.of(
                new CategoryData(springId, "Spring", List.of(
                        new CategoryData(jpaId, "JPA", List.of(
                                new CategoryData(n1Id, "N + 1", List.of())
                        )),
                        new CategoryData(securityId, "Security", List.of(
                                new CategoryData(oAuthId, "OAuth", List.of()),
                                new CategoryData(csrfId, "CSRF", List.of())
                        ))
                )),
                new CategoryData(algorithmId, "Algorithm", List.of(
                        new CategoryData(dfsId, "DFS", List.of())
                ))
        );

        // when
        List<CategoryData> allByMemberId = categoryQueryService.findAllByBlogId(말랑_블로그_ID);

        // then
        assertThat(allByMemberId).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
