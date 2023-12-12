package com.mallang.post.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.CreatePostCategoryCommand;
import com.mallang.post.query.response.PostCategoryResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 카테고리 조회 서비스 (PostCategoryQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostCategoryQueryServiceTest extends ServiceTest {

    @Test
    void 카테고리_목록이_없는_경우_빈_리스트_반환() {
        // given
        var memberId = 회원을_저장한다("동훈");
        var 동훈_블로그_이름 = 블로그_개설(memberId, "donghun");

        // when
        List<PostCategoryResponse> result = postCategoryQueryService.findAllByBlogName(동훈_블로그_이름);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 특정_블로그의_카테고리를_순서대로_전체_조회한다() {
        // given
        Long 동훈_ID = 회원을_저장한다("동훈");
        String 동훈_블로그_이름 = 블로그_개설(동훈_ID, "donghun");
        postCategoryService.create(new CreatePostCategoryCommand(
                동훈_ID,
                동훈_블로그_이름,
                "Node",
                null,
                null,
                null
        ));

        Long 말랑_ID = 회원을_저장한다("말랑");
        String 말랑_블로그_이름 = 블로그_개설(말랑_ID, "mallang");
        Long springId = postCategoryService.create(new CreatePostCategoryCommand(
                말랑_ID,
                말랑_블로그_이름,
                "Spring",
                null,
                null,
                null
        ));
        Long jpaId = postCategoryService.create(new CreatePostCategoryCommand(
                말랑_ID,
                말랑_블로그_이름,
                "JPA",
                springId,
                null,
                null
        ));
        Long n1Id = postCategoryService.create(new CreatePostCategoryCommand(
                말랑_ID,
                말랑_블로그_이름,
                "N + 1",
                jpaId,
                null,
                null
        ));
        Long securityId = postCategoryService.create(new CreatePostCategoryCommand(
                말랑_ID,
                말랑_블로그_이름,
                "Security",
                springId,
                jpaId,
                null
        ));
        Long oAuthId = postCategoryService.create(new CreatePostCategoryCommand(
                말랑_ID,
                말랑_블로그_이름,
                "OAuth",
                securityId,
                null,
                null
        ));
        Long csrfId = postCategoryService.create(new CreatePostCategoryCommand(
                말랑_ID,
                말랑_블로그_이름,
                "CSRF",
                securityId,
                null,
                oAuthId
        ));
        Long algorithmId = postCategoryService.create(new CreatePostCategoryCommand(
                말랑_ID,
                말랑_블로그_이름,
                "Algorithm",
                null,
                null,
                springId
        ));
        Long dfsId = postCategoryService.create(new CreatePostCategoryCommand(
                말랑_ID,
                말랑_블로그_이름,
                "DFS",
                algorithmId,
                null,
                null
        ));
        List<PostCategoryResponse> expected = List.of(
                new PostCategoryResponse(algorithmId, "Algorithm", List.of(
                        new PostCategoryResponse(dfsId, "DFS", List.of())
                )),
                new PostCategoryResponse(springId, "Spring", List.of(
                        new PostCategoryResponse(jpaId, "JPA", List.of(
                                new PostCategoryResponse(n1Id, "N + 1", List.of())
                        )),
                        new PostCategoryResponse(securityId, "Security", List.of(
                                new PostCategoryResponse(csrfId, "CSRF", List.of()),
                                new PostCategoryResponse(oAuthId, "OAuth", List.of())
                        ))
                ))
        );

        // when
        List<PostCategoryResponse> allByMemberId = postCategoryQueryService.findAllByBlogName(말랑_블로그_이름);

        // then
        assertThat(allByMemberId).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
