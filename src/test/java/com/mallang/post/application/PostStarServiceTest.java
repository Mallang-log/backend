package com.mallang.post.application;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static com.mallang.post.PostFixture.privatePost;
import static com.mallang.post.PostFixture.publicPost;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.CancelPostStarCommand;
import com.mallang.post.application.command.StarPostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.domain.star.PostStar;
import com.mallang.post.domain.star.PostStarRepository;
import com.mallang.post.domain.star.StarGroup;
import com.mallang.post.domain.star.StarGroupRepository;
import com.mallang.post.exception.NoAuthorityPostException;
import com.mallang.post.exception.NoAuthorityStarGroupException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("포스트 즐겨찾기 서비스 (PostStarService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostStarServiceTest extends ServiceTest {

    private final PostRepository postRepository = mock(PostRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final PostStarRepository postStarRepository = mock(PostStarRepository.class);
    private final StarGroupRepository starGroupRepository = mock(StarGroupRepository.class);
    private final PostStarService postStarService = new PostStarService(
            postRepository,
            memberRepository,
            postStarRepository,
            starGroupRepository
    );

    private final Member member = 깃허브_말랑(1L);
    private final Member other = 깃허브_동훈(2L);
    private final Blog blog = mallangBlog(member);
    private final Long postId = 10L;
    private final Post post = publicPost(postId, blog);
    private final StarGroup starGroup = new StarGroup("spring", member);

    @BeforeEach
    void setUp() {
        given(memberRepository.getById(member.getId())).willReturn(member);
        given(memberRepository.getById(other.getId())).willReturn(other);
        given(postRepository.getById(postId, blog.getName())).willReturn(post);
        ReflectionTestUtils.setField(starGroup, "id", 1L);
        given(starGroupRepository.getByIdIfIdNotNull(starGroup.getId())).willReturn(starGroup);
        given(starGroupRepository.getByIdIfIdNotNull(null)).willReturn(null);
    }

    @Nested
    class 즐겨찾기_시 {

        @Test
        void 포스트_접근_권한이_없으면_예외() {
            // given
            Post post = privatePost(postId, blog);
            given(postRepository.getById(postId, blog.getName())).willReturn(post);
            var command = new StarPostCommand(
                    postId,
                    blog.getName(),
                    starGroup.getId(),
                    other.getId(),
                    null
            );

            // when & then
            assertThatThrownBy(() -> {
                postStarService.star(command);
            }).isInstanceOf(NoAuthorityPostException.class);
        }

        @Test
        void 즐겨찾기를_한다() {
            // given
            PostStar postStar = new PostStar(post, member);
            given(postStarRepository.findByPostAndMember(post, member))
                    .willReturn(empty());
            given(postStarRepository.save(any()))
                    .willReturn(postStar);
            var command = new StarPostCommand(
                    postId,
                    blog.getName(),
                    starGroup.getId(),
                    member.getId(),
                    null
            );

            // when
            Long starId = postStarService.star(command);

            // then
            then(postStarRepository)
                    .should(times(1))
                    .save(any());
            assertThat(postStar.getStarGroup()).isEqualTo(starGroup);
        }

        @Test
        void 회원이_이미_해당_포스트에_즐겨찾기를_누른_경우_그룹이_업데이트된다() {
            // given
            PostStar postStar = new PostStar(post, member);
            given(postStarRepository.findByPostAndMember(post, member))
                    .willReturn(of(postStar));
            var command = new StarPostCommand(
                    postId,
                    blog.getName(),
                    starGroup.getId(),
                    member.getId(),
                    null
            );

            // when
            Long starId = postStarService.star(command);

            // then
            then(postStarRepository)
                    .should(times(0))
                    .save(any());
            assertThat(postStar.getStarGroup()).isEqualTo(starGroup);
        }

        @Test
        void 타인의_즐겨찾기_그룹을_설정한_경우_예외() {
            // given
            PostStar postStar = new PostStar(post, member);
            given(postStarRepository.findByPostAndMember(post, member)).willReturn(empty());
            given(postStarRepository.save(any())).willReturn(postStar);
            StarGroup otherGroup = new StarGroup("spring", other);
            given(starGroupRepository.getByIdIfIdNotNull(1L)).willReturn(otherGroup);

            var command = new StarPostCommand(
                    postId,
                    blog.getName(),
                    1L,
                    member.getId(),
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    postStarService.star(command)
            ).isInstanceOf(NoAuthorityStarGroupException.class);
        }
    }

    @Nested
    class 즐겨찾기_취소_시 {

        @Test
        void 즐겨찾기_취소_시_즐겨찾기가_제거된다() {
            // given
            PostStar star = new PostStar(post, member);
            given(postStarRepository.getByPostAndMember(
                    postId,
                    blog.getName(),
                    member.getId()
            )).willReturn(star);
            var command = new CancelPostStarCommand(member.getId(), postId, blog.getName());

            // when
            postStarService.cancel(command);

            // then
            then(postStarRepository)
                    .should(times(1))
                    .delete(star);
        }

        @Test
        void 보호글_비공개글_여부에_관계없이_취소할_수_있다() {
            // given
            Post post = privatePost(postId, blog);
            PostStar star = new PostStar(post, other);
            given(postStarRepository.getByPostAndMember(
                    postId,
                    blog.getName(),
                    other.getId()
            )).willReturn(star);
            var command = new CancelPostStarCommand(other.getId(), postId, blog.getName());

            // when
            postStarService.cancel(command);

            // then
            then(postStarRepository)
                    .should(times(1))
                    .delete(star);
        }
    }
}
