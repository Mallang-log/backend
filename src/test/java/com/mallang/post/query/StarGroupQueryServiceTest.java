package com.mallang.post.query;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.CreateStarGroupCommand;
import com.mallang.post.query.response.StarGroupListResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("즐겨찾기 그룹 조회 서비스 (StarGroupQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class StarGroupQueryServiceTest extends ServiceTest {

    @Test
    void 즐겨찾기_목록이_없는_경우_빈_리스트_반환() {
        // given
        var memberId = 회원을_저장한다("동훈");

        // when
        List<StarGroupListResponse> result = starGroupQueryService.findAllByMember(memberId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 회원의_즐겨찾기_그룹_목록을_계층에_맞게_정렬하여_보여준다() {
        // given
        var memberId = 회원을_저장한다("동훈");
        var otherMemberId = 회원을_저장한다("other");
        var otherRootId = starGroupService.create(new CreateStarGroupCommand(
                otherMemberId,
                "다른사람 루트",
                null,
                null,
                null
        ));

        var root3Id = starGroupService.create(new CreateStarGroupCommand(
                memberId,
                "루트3",
                null,
                null,
                null
        ));
        var root1Id = starGroupService.create(new CreateStarGroupCommand(
                memberId,
                "루트1",
                null,
                null,
                root3Id
        ));
        var root2Id = starGroupService.create(new CreateStarGroupCommand(
                memberId,
                "루트2",
                null,
                root1Id,
                root3Id
        ));

        var root2_child2_Id = starGroupService.create(new CreateStarGroupCommand(
                memberId,
                "루트2 자식2",
                root2Id,
                null,
                null
        ));

        var root2_child1_Id = starGroupService.create(new CreateStarGroupCommand(
                memberId,
                "루트2 자식1",
                root2Id,
                null,
                root2_child2_Id
        ));

        var root2_child2_child1_Id = starGroupService.create(new CreateStarGroupCommand(
                memberId,
                "루트2 자식2 자식1",
                root2_child2_Id,
                null,
                null
        ));

        var root2_child2_child2_Id = starGroupService.create(new CreateStarGroupCommand(
                memberId,
                "루트2 자식2 자식2",
                root2_child2_Id,
                root2_child2_child1_Id,
                null
        ));

        var root2_child1_child2_Id = starGroupService.create(new CreateStarGroupCommand(
                memberId,
                "루트2 자식1 자식2",
                root2_child1_Id,
                null,
                null
        ));
        var root2_child1_child1_Id = starGroupService.create(new CreateStarGroupCommand(
                memberId,
                "루트2 자식1 자식1",
                root2_child1_Id,
                null,
                root2_child1_child2_Id
        ));
        var expected = List.of(
                new StarGroupListResponse(
                        root1Id,
                        "루트1",
                        null,
                        null,
                        root2Id,
                        emptyList()
                ),
                new StarGroupListResponse(
                        root2Id,
                        "루트2",
                        null,
                        root1Id,
                        root3Id,
                        List.of(
                                new StarGroupListResponse(
                                        root2_child1_Id,
                                        "루트2 자식1",
                                        root2Id,
                                        null,
                                        root2_child2_Id,
                                        List.of(
                                                new StarGroupListResponse(
                                                        root2_child1_child1_Id,
                                                        "루트2 자식1 자식1",
                                                        root2_child1_Id,
                                                        null,
                                                        root2_child1_child2_Id,
                                                        emptyList()
                                                ),
                                                new StarGroupListResponse(
                                                        root2_child1_child2_Id,
                                                        "루트2 자식1 자식2",
                                                        root2_child1_Id,
                                                        root2_child1_child1_Id,
                                                        null,
                                                        emptyList()
                                                )
                                        )),
                                new StarGroupListResponse(
                                        root2_child2_Id,
                                        "루트2 자식2",
                                        root2Id,
                                        root2_child1_Id,
                                        null,
                                        List.of(
                                                new StarGroupListResponse(
                                                        root2_child2_child1_Id,
                                                        "루트2 자식2 자식1",
                                                        root2_child2_Id,
                                                        null,
                                                        root2_child2_child2_Id,
                                                        emptyList()
                                                ),
                                                new StarGroupListResponse(
                                                        root2_child2_child2_Id,
                                                        "루트2 자식2 자식2",
                                                        root2_child2_Id,
                                                        root2_child2_child1_Id,
                                                        null,
                                                        emptyList()
                                                )
                                        ))
                        )),
                new StarGroupListResponse(
                        root3Id,
                        "루트3",
                        null,
                        root2Id,
                        null,
                        emptyList()
                )
        );

        // when
        List<StarGroupListResponse> result = starGroupQueryService.findAllByMember(memberId);

        // then
        assertThat(result)
                .hasSize(3)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
