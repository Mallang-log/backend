package com.mallang.comment;

import com.mallang.auth.domain.Member;
import com.mallang.comment.domain.AuthComment;
import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.UnAuthComment;
import com.mallang.post.domain.Post;
import jakarta.annotation.Nullable;
import org.springframework.test.util.ReflectionTestUtils;

public class CommentFixture {

    public static AuthComment authComment(
            Long id,
            String content,
            Post post,
            @Nullable Comment parent,
            boolean secret,
            Member writer
    ) {
        AuthComment comment = new AuthComment(content, post, parent, secret, writer);
        ReflectionTestUtils.setField(comment, "id", id);
        return comment;
    }

    public static UnAuthComment unAuthComment(
            Long id,
            String content,
            Post post,
            @Nullable Comment parent,
            String nickname,
            String password
    ) {
        UnAuthComment unAuthComment = new UnAuthComment(content, post, parent, nickname, password);
        ReflectionTestUtils.setField(unAuthComment, "id", id);
        return unAuthComment;
    }
}
