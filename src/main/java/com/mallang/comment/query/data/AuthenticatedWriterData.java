package com.mallang.comment.query.data;

import static lombok.AccessLevel.PRIVATE;

import com.mallang.member.domain.Member;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = PRIVATE)
public class AuthenticatedWriterData implements CommentWriterData {

    private String type = AUTHENTICATED_WRITER_DATA_TYPE;
    private Long memberId;
    private String nickname;
    private String profileImageUrl;

    @Builder
    public AuthenticatedWriterData(Long memberId, String nickname, String profileImageUrl) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public static AuthenticatedWriterData anonymous() {
        return AuthenticatedWriterData.builder()
                .nickname("익명")
                .build();
    }

    public static AuthenticatedWriterData from(Member member) {
        return AuthenticatedWriterData.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
