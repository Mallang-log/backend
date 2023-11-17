package com.mallang.auth.domain;

import static lombok.AccessLevel.PROTECTED;

import com.mallang.common.domain.CommonDomainModel;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = PROTECTED)
@Table(name = "member",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "oauth_id_unique",
                        columnNames = {
                                "oauth_server_id",
                                "oauth_server"
                        }
                ),
        }
)
@Entity
public class Member extends CommonDomainModel {

    @Embedded
    private OauthId oauthId;
    private String nickname;
    private String profileImageUrl;

    public Member(OauthId oauthId, String nickname, String profileImageUrl) {
        this.oauthId = oauthId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public OauthId getOauthId() {
        return oauthId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
