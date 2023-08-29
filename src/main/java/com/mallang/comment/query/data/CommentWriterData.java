package com.mallang.comment.query.data;

import static com.mallang.comment.query.data.CommentWriterData.AUTHENTICATED_WRITER_DATA_TYPE;
import static com.mallang.comment.query.data.CommentWriterData.UNAUTHENTICATED_WRITER_DATA_TYPE;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mallang.comment.domain.writer.AuthenticatedWriter;
import com.mallang.comment.domain.writer.CommentWriter;
import com.mallang.comment.domain.writer.UnAuthenticatedWriter;
import com.mallang.common.execption.MallangLogException;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AuthenticatedWriterData.class, name = AUTHENTICATED_WRITER_DATA_TYPE),
        @JsonSubTypes.Type(value = UnAuthenticatedWriterData.class, name = UNAUTHENTICATED_WRITER_DATA_TYPE),
})
public interface CommentWriterData {

    String AUTHENTICATED_WRITER_DATA_TYPE = "AuthenticatedWriter";
    String UNAUTHENTICATED_WRITER_DATA_TYPE = "UnAuthenticatedWriter";

    static CommentWriterData from(CommentWriter commentWriter) {
        if (commentWriter instanceof AuthenticatedWriter authed) {
            return AuthenticatedWriterData.from(authed.getMember());
        }
        if (commentWriter instanceof UnAuthenticatedWriter unAuthed) {
            return UnAuthenticatedWriterData.from(unAuthed);
        }
        throw new MallangLogException("해당 CommentWriter 타입이 CommentWriterData 에서 지원되지 않습니다.");
    }
}
