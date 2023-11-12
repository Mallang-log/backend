package com.mallang.comment.query.data;

import static com.mallang.comment.query.data.CommentData.AUTHENTICATED_COMMENT_DATA_TYPE;
import static com.mallang.comment.query.data.CommentData.UNAUTHENTICATED_COMMENT_DATA_TYPE;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mallang.comment.domain.AuthenticatedComment;
import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.UnAuthenticatedComment;
import com.mallang.common.execption.MallangLogException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AuthenticatedCommentData.class, name = AUTHENTICATED_COMMENT_DATA_TYPE),
        @JsonSubTypes.Type(value = UnAuthenticatedCommentData.class, name = UNAUTHENTICATED_COMMENT_DATA_TYPE),
})
@Data
public abstract class CommentData {

    protected Long id;
    protected String content;
    protected LocalDateTime createdDate;
    protected boolean deleted;
    protected List<CommentData> children;

    protected CommentData(
            Long id,
            String content,
            LocalDateTime createdDate,
            boolean deleted,
            List<CommentData> children
    ) {
        this.id = id;
        this.content = content;
        this.createdDate = createdDate;
        this.deleted = deleted;
        this.children = (children == null)
                ? new ArrayList<>()
                : children;
    }

    public static final String AUTHENTICATED_COMMENT_DATA_TYPE = "AuthenticatedComment";
    public static final String UNAUTHENTICATED_COMMENT_DATA_TYPE = "UnAuthenticatedComment";

    public static CommentData from(Comment comment) {
        if (comment instanceof AuthenticatedComment authed) {
            return AuthenticatedCommentData.from(authed);
        }
        if (comment instanceof UnAuthenticatedComment unAuthed) {
            return UnAuthenticatedCommentData.from(unAuthed);
        }
        throw new MallangLogException("해당 Comment 타입이 CommentData 에서 지원되지 않습니다.");
    }
}
