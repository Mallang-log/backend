package com.mallang.post.domain.star;

import com.mallang.auth.domain.Member;
import com.mallang.category.TieredCategoryTestTemplate;
import com.mallang.post.exception.NoAuthorityStarGroupException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;

@DisplayName("즐겨찾기 그룹 (StarGroup) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class StarGroupTest extends TieredCategoryTestTemplate<StarGroup> {

    @Override
    protected StarGroup createRoot(String name, Member owner) {
        return new StarGroup(name, owner);
    }

    @Override
    protected StarGroup createChild(String name, Member owner, StarGroup parent) {
        return createChild(name, owner, parent, null, null);
    }

    @Override
    protected StarGroup createChild(
            String name,
            Member owner,
            StarGroup parent,
            StarGroup prev,
            StarGroup next
    ) {
        StarGroup starGroup = new StarGroup(name, owner);
        starGroup.updateHierarchy(parent, prev, next);
        return starGroup;
    }

    @Override
    protected Class<?> 권한_없음_예외() {
        return NoAuthorityStarGroupException.class;
    }
}
