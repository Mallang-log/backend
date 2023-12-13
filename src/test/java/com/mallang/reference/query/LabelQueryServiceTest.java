package com.mallang.reference.query;

import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.reference.domain.Label;
import com.mallang.reference.query.repository.LabelQueryRepository;
import com.mallang.reference.query.response.LabelListResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("라벨 조회 서비스 (LabelQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class LabelQueryServiceTest {

    private final Long memberId = 1L;
    private final Member member = 깃허브_말랑(1L);
    private final LabelQueryRepository labelQueryRepository = mock(LabelQueryRepository.class);
    private final LabelQueryService labelQueryService = new LabelQueryService(labelQueryRepository);

    @Test
    void 내_라벨이_없으면_빈_리스트를_반환한다() {
        // given
        given(labelQueryRepository.findAllByOwnerId(memberId))
                .willReturn(emptyList());

        // when
        List<LabelListResponse> result = labelQueryService.findAllByMemberId(memberId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 내_라벨을_순서대로_전체_조회한다() {
        // given
        Label first = new Label("first", member, "#000000");
        Label third = new Label("third", member, "#000000");
        Label forth = new Label("forth", member, "#000000");
        Label second = new Label("second", member, "#000000");
        third.updateHierarchy(first, null);
        second.updateHierarchy(first, third);
        forth.updateHierarchy(third, null);
        ReflectionTestUtils.setField(first, "id", 1L);
        ReflectionTestUtils.setField(third, "id", 2L);
        ReflectionTestUtils.setField(forth, "id", 3L);
        ReflectionTestUtils.setField(second, "id", 4L);

        List<Label> labels = List.of(
                forth,
                first,
                second,
                third
        );
        given(labelQueryRepository.findAllByOwnerId(memberId))
                .willReturn(labels);

        var expected = List.of(
                new LabelListResponse(
                        1L,
                        "first",
                        "#000000",
                        null,
                        4L
                ),
                new LabelListResponse(
                        4L,
                        "second",
                        "#000000",
                        1L,
                        2L
                ),
                new LabelListResponse(
                        2L,
                        "third",
                        "#000000",
                        4L,
                        3L
                ),
                new LabelListResponse(
                        3L,
                        "forth",
                        "#000000",
                        2L,
                        null
                )
        );

        // when
        List<LabelListResponse> result = labelQueryService.findAllByMemberId(memberId);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
