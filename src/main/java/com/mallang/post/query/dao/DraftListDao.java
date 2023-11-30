package com.mallang.post.query.dao;


import com.mallang.blog.domain.Blog;
import com.mallang.post.query.response.DraftListResponse;
import com.mallang.post.query.support.DraftQuerySupport;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DraftListDao {

    private final DraftQuerySupport draftQuerySupport;

    public List<DraftListResponse> find(Blog blog) {
        return draftQuerySupport.findAllByBlogOrderByUpdatedDateDesc(blog)
                .stream()
                .map(DraftListResponse::from)
                .toList();
    }
}
