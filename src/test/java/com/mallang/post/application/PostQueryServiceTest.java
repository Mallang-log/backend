package com.mallang.post.application;

import static com.mallang.member.domain.OauthServerType.GITHUB;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.member.domain.Member;
import com.mallang.member.domain.MemberRepository;
import com.mallang.member.domain.OauthId;
import com.mallang.post.application.command.CreatePostCommand;
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
    private PostQueryService postQueryService;

    @Autowired
    private PostService postService;

    @Autowired
    private MemberRepository memberRepository;

    private Long memberId;

    @BeforeEach
    void setUp() {
        memberId = memberRepository.save(Member.builder()
                .oauthId(new OauthId("1", GITHUB))
                .nickname("말랑")
                .profileImageUrl("https://mallang.com")
                .build()
        ).getId();
    }

    @Test
    void 게시글을_조회한다() {
        // given
        CreatePostCommand command = new CreatePostCommand(memberId, "게시글 1", "content");
        Long id = postService.create(command);

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
        CreatePostCommand request1 = new CreatePostCommand(memberId, "게시글1", "content1");
        CreatePostCommand request2 = new CreatePostCommand(memberId, "게시글2", "content2");
        Long post1Id = postService.create(request1);
        Long post2Id = postService.create(request2);

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
