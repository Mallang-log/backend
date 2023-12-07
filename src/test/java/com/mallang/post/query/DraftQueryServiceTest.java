package com.mallang.post.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.category.application.command.CreateCategoryCommand;
import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.CreateDraftCommand;
import com.mallang.post.exception.NoAuthorityDraftException;
import com.mallang.post.query.response.DraftDetailResponse;
import com.mallang.post.query.response.DraftListResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("임시 글 조회 서비스 (DraftQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class DraftQueryServiceTest extends ServiceTest {

    private Long memberId;
    private Long otherMemberId;
    private String blogName;
    private Long categoryId;
    private Long 임시_글_1_ID;
    private Long 임시_글_2_ID;

    @BeforeEach
    void setUp() {
        memberId = 회원을_저장한다("말랑");
        otherMemberId = 회원을_저장한다("other");
        blogName = 블로그_개설(memberId, "mallang-log");
        categoryId = categoryService.create(new CreateCategoryCommand(
                memberId,
                blogName,
                "Spring",
                null,
                null,
                null
        ));
        CreateDraftCommand 임시_글_1_요청 = CreateDraftCommand.builder()
                .memberId(memberId)
                .blogName(blogName)
                .title("임시 글 1")
                .bodyText("bodyText")
                .intro("intro")
                .tags(List.of("tag1", "tag2", "tag3"))
                .categoryId(categoryId)
                .build();
        임시_글_1_ID = draftService.create(임시_글_1_요청);

        CreateDraftCommand 임시_글_2_요청 = CreateDraftCommand.builder()
                .memberId(memberId)
                .blogName(blogName)
                .title("임시 글 2")
                .bodyText("bodyText")
                .intro("intro")
                .build();
        임시_글_2_ID = draftService.create(임시_글_2_요청);
    }

    @Nested
    class 임시_글_목록_조회_시 {

        @Test
        void 작성한_임시_글_목록을_조회한다() {
            // when
            List<DraftListResponse> result = draftQueryService.findAllByBlog(memberId, blogName);

            // then
            assertThat(result)
                    .extracting(DraftListResponse::title)
                    .containsExactly("임시 글 2", "임시 글 1");
        }

        @Test
        void 블로그_주인이_아니면_예외() {
            // when & then
            assertThatThrownBy(() ->
                    draftQueryService.findAllByBlog(otherMemberId, blogName)
            ).isInstanceOf(NoAuthorityBlogException.class);
        }
    }

    @Nested
    class 임시_글_단일_조회_시 {

        @Test
        void 단일_조회한다() {
            // when
            DraftDetailResponse response = draftQueryService.findById(memberId, 임시_글_1_ID);

            // then
            assertThat(response.title()).isEqualTo("임시 글 1");
        }

        @Test
        void 작성자가_아니면_예외() {
            // when & then
            assertThatThrownBy(() ->
                    draftQueryService.findById(otherMemberId, 임시_글_1_ID)
            ).isInstanceOf(NoAuthorityDraftException.class);
        }
    }
}
