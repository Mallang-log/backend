package com.mallang.post.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.member.MemberServiceHelper;
import com.mallang.post.application.query.PostDetailResponse;
import com.mallang.post.application.query.PostSimpleResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("포스트 조회 서비스(PostQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Transactional
@SpringBootTest
class PostQueryServiceTest {

    @Autowired
    private PostServiceTestHelper postServiceTestHelper;

    @Autowired
    private MemberServiceHelper memberServiceHelper;

    @Autowired
    private PostQueryService postQueryService;

    private Long memberId;

    @BeforeEach
    void setUp() {
        memberId = memberServiceHelper.회원을_저장한다("말랑");
    }

    @Test
    void 게시글을_조회한다() {
        // given
        Long id = postServiceTestHelper.포스트를_저장한다(memberId, "게시글 1", "content");

        // when
        PostDetailResponse response = postQueryService.getById(id);

        // then
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.writerId()).isEqualTo(memberId);
        assertThat(response.writerNickname()).isEqualTo("말랑");
        assertThat(response.title()).isEqualTo("게시글 1");
        assertThat(response.content()).isEqualTo("content");
        assertThat(response.createdDate()).isNotNull();
    }

    @Test
    void 게시글을_전체_조회한다() {
        // given
        Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, "게시글1", "content1");
        Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, "게시글2", "content2");

        // when
        List<PostSimpleResponse> responses = postQueryService.findAll();

        // then
        assertThat(responses).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(List.of(
                                PostSimpleResponse.builder()
                                        .id(post1Id)
                                        .writerId(memberId)
                                        .writerNickname("말랑")
                                        .title("게시글1")
                                        .content("content1")
                                        .build(),
                                PostSimpleResponse.builder()
                                        .id(post2Id)
                                        .writerId(memberId)
                                        .writerNickname("말랑")
                                        .title("게시글2")
                                        .content("content2")
                                        .build()
                        )
                );
    }
}
