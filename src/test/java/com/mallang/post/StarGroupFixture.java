package com.mallang.post;

import com.mallang.auth.domain.Member;
import com.mallang.post.domain.star.StarGroup;
import org.springframework.test.util.ReflectionTestUtils;

public class StarGroupFixture {

    public static StarGroup starGroup(Long id, String name, Member member) {
        StarGroup starGroup = new StarGroup(name, member);
        ReflectionTestUtils.setField(starGroup, "id", id);
        return starGroup;
    }
}
