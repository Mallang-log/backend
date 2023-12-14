package com.mallang.comment.query.response;

import static com.mallang.comment.query.response.CommentResponse.AUTH_COMMENT_DATA_TYPE;
import static com.mallang.comment.query.response.CommentResponse.UN_AUTH_COMMENT_DATA_TYPE;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
        @JsonSubTypes.Type(value = AuthCommentResponse.class, name = AUTH_COMMENT_DATA_TYPE),
        @JsonSubTypes.Type(value = UnAuthCommentResponse.class, name = UN_AUTH_COMMENT_DATA_TYPE),
})
@Data
public abstract sealed class CommentResponse
        permits AuthCommentResponse, UnAuthCommentResponse {

    public static final String AUTH_COMMENT_DATA_TYPE = "AuthComment";
    public static final String UN_AUTH_COMMENT_DATA_TYPE = "UnAuthComment";

    protected final Long id;
    protected final String content;
    protected final LocalDateTime createdDate;
    protected final boolean deleted;
    protected final List<CommentResponse> children = new ArrayList<>();

    protected CommentResponse(
            Long id,
            String content,
            LocalDateTime createdDate,
            boolean deleted
    ) {
        this.id = id;
        this.content = content;
        this.createdDate = createdDate;
        this.deleted = deleted;
    }

    public void setChildren(List<CommentResponse> children) {
        this.children.clear();
        this.children.addAll(children);
    }
}
