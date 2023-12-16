package com.mallang.post;

import com.mallang.blog.domain.Blog;
import com.mallang.post.domain.draft.Draft;
import java.util.Collections;
import org.springframework.test.util.ReflectionTestUtils;

public class DraftFixture {

    public static Draft draft(Long id, Blog blog) {
        Draft draft = new Draft(
                blog,
                "title",
                "intro",
                "content",
                null,
                null,
                Collections.emptyList(),
                blog.getOwner()
        );
        ReflectionTestUtils.setField(draft, "id", 1L);
        return draft;
    }
}
