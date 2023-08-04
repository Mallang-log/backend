package com.mallang.post.application;

import static com.mallang.member.domain.OauthServerType.GITHUB;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.member.domain.Member;
import com.mallang.member.domain.MemberRepository;
import com.mallang.member.domain.OauthId;
import com.mallang.post.application.command.CreatePostCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("포스트 서비스(PostService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Transactional
@SpringBootTest
class PostServiceTest {

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
    void 게시글을_저장한다() {
        // given
        CreatePostCommand command = new CreatePostCommand(memberId, "게시글 1", "content");

        // when
        Long id = postService.create(command);

        // then
        assertThat(id).isNotNull();
    }
}
