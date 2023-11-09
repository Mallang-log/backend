package com.mallang.post.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.post.domain.visibility.PostVisibility.Visibility;
import com.mallang.post.query.data.PostDetailData;
import com.mallang.post.query.data.PostDetailData.WriterDetailInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

@DisplayName("포스트 조회 데이터 보호기(PostDataProtector) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostDataProtectorTest {

    private final PostDataProtector postDataProtector = new PostDataProtector();

    @Test
    void 보호_글이라도_글의_주인이_조회한다면_내용을_다_보여준다() {
        // given
        Long memberId = 1L;
        PostDetailData postDetailData = PostDetailData.builder()
                .writerInfo(new WriterDetailInfo(memberId, "mallang", "url"))
                .content("content")
                .visibility(Visibility.PROTECTED)
                .build();

        // when
        PostDetailData protectedData = postDataProtector.protectIfRequired(memberId, postDetailData);

        // then
        assertThat(protectedData.content()).isEqualTo("content");
    }

    @Test
    void 글의_주인이_아닌_다른_사람이_보호_글을_조회한다면_보호된다() {
        // given
        Long memberId = 1L;
        PostDetailData postDetailData = PostDetailData.builder()
                .writerInfo(new WriterDetailInfo(memberId, "mallang", "url"))
                .content("content")
                .visibility(Visibility.PROTECTED)
                .build();

        // when
        PostDetailData protectedData = postDataProtector.protectIfRequired(memberId + 1, postDetailData);

        // then
        assertThat(protectedData.content()).isEqualTo("보호되어 있는 글입니다. 내용을 보시려면 비밀번호를 입력하세요.");
    }

    @ParameterizedTest
    @EnumSource(mode = Mode.EXCLUDE, value = Visibility.class, names = {"PROTECTED"})
    void 보호되지_않은_글은_모두에게_전체_공개될_수_있다(Visibility visibility) {
        // given
        Long memberId = 1L;
        PostDetailData postDetailData = PostDetailData.builder()
                .writerInfo(new WriterDetailInfo(memberId, "mallang", "url"))
                .content("content")
                .visibility(visibility)
                .build();

        // when
        PostDetailData protectedData = postDataProtector.protectIfRequired(null, postDetailData);

        // then
        assertThat(protectedData.content()).isEqualTo("content");
    }
}
