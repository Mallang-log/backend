package com.mallang.post.query.repository;

import com.mallang.post.domain.Post;
import com.mallang.post.query.data.PostSearchCond;
import java.util.List;

public interface PostQueryDslRepository {

    List<Post> search(PostSearchCond cond);
}
