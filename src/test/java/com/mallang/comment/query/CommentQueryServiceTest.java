package com.mallang.comment.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

import com.mallang.comment.application.CommentServiceTestHelper;
import com.mallang.comment.query.data.AuthenticatedCommentData;
import com.mallang.comment.query.data.CommentData;
import com.mallang.comment.query.data.UnAuthenticatedCommentData;
import com.mallang.member.MemberServiceTestHelper;
import com.mallang.post.application.PostServiceTestHelper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@DisplayName("CommentQuery 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@SpringBootTest
class CommentQueryServiceTest {

    @DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
    @SpringBootTest
    @Nested
    class 특정_포스트의_댓글_모두_조회_시 {

        @Autowired
        private MemberServiceTestHelper memberServiceTestHelper;

        @Autowired
        private PostServiceTestHelper postServiceTestHelper;

        @Autowired
        private CommentServiceTestHelper commentServiceTestHelper;

        @Autowired
        private CommentQueryService commentQueryService;


        @Test
        void 인증되지_않은_요청인_경우_비밀_댓글은_비밀_댓글로_조회된다() {
            // given
            Long mallang = memberServiceTestHelper.회원을_저장한다("말랑");
            Long postId = postServiceTestHelper.포스트를_저장한다(mallang, "포스트", "내용");
            commentServiceTestHelper.댓글을_작성한다(postId, "댓글1", false, mallang);
            commentServiceTestHelper.댓글을_작성한다(postId, "[비밀] 댓글2", true, mallang);
            commentServiceTestHelper.비인증_댓글을_작성한다(postId, "댓글3", "랑말", "1234");

            // when
            List<CommentData> result = commentQueryService.findAllByPostId(postId, null);

            // then
            assertThat(result)
                    .extracting(CommentData::getContent)
                    .containsExactly("댓글1", "비밀 댓글입니다.", "댓글3");
            assertThat(result)
                    .filteredOn(it -> it instanceof AuthenticatedCommentData)
                    .map(it -> (AuthenticatedCommentData) it)
                    .extracting(it -> it.getWriterData().nickname())
                    .containsExactly("말랑", "익명");
            assertThat(result)
                    .filteredOn(it -> it instanceof UnAuthenticatedCommentData)
                    .map(it -> (UnAuthenticatedCommentData) it)
                    .extracting(it -> it.getWriterData().nickname())
                    .containsExactly("랑말");
        }

        @Test
        void 내가_쓴_비밀_댓글만_보여지게_조회() {
            // given
            Long mallang = memberServiceTestHelper.회원을_저장한다("말랑");
            Long postId = postServiceTestHelper.포스트를_저장한다(mallang, "포스트", "내용");
            Long dong = memberServiceTestHelper.회원을_저장한다("동훈");
            Long hehe = memberServiceTestHelper.회원을_저장한다("헤헤");
            commentServiceTestHelper.댓글을_작성한다(postId, "동훈 댓글", false, dong);
            commentServiceTestHelper.댓글을_작성한다(postId, "헤헤 댓글", false, hehe);
            commentServiceTestHelper.댓글을_작성한다(postId, "[비밀] 동훈 댓글2", true, dong); // 제외
            commentServiceTestHelper.댓글을_작성한다(postId, "[비밀] 헤헤 댓글2", true, hehe);
            commentServiceTestHelper.비인증_댓글을_작성한다(postId, "댓글3", "랑말", "1234");

            // when
            List<CommentData> result = commentQueryService.findAllByPostId(postId, dong);

            // then
            assertThat(result)
                    .extracting(CommentData::getContent)
                    .containsExactly("동훈 댓글", "헤헤 댓글", "[비밀] 동훈 댓글2", "비밀 댓글입니다.", "댓글3");
        }

        @Test
        void 글_작성자는_모든_비밀_댓글을_볼_수_있다() {
            // given
            Long mallang = memberServiceTestHelper.회원을_저장한다("말랑");
            Long postId = postServiceTestHelper.포스트를_저장한다(mallang, "포스트", "내용");
            Long dong = memberServiceTestHelper.회원을_저장한다("동훈");
            Long hehe = memberServiceTestHelper.회원을_저장한다("헤헤");
            commentServiceTestHelper.댓글을_작성한다(postId, "동훈 댓글", false, mallang);
            commentServiceTestHelper.댓글을_작성한다(postId, "헤헤 댓글", false, hehe);
            commentServiceTestHelper.댓글을_작성한다(postId, "[비밀] 동훈 댓글2", true, mallang); // 제외
            commentServiceTestHelper.댓글을_작성한다(postId, "[비밀] 헤헤 댓글2", true, hehe);
            commentServiceTestHelper.비인증_댓글을_작성한다(postId, "댓글3", "랑말", "1234");

            // when
            List<CommentData> result = commentQueryService.findAllByPostId(postId, mallang);

            // then
            assertThat(result)
                    .extracting(CommentData::getContent)
                    .containsExactly("동훈 댓글", "헤헤 댓글", "[비밀] 동훈 댓글2", "[비밀] 헤헤 댓글2", "댓글3");
        }
    }
}
