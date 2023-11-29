package com.mallang.auth.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "oauth_member",
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
@DiscriminatorValue("oauth")
@Entity
public class OauthMember extends Member {

    @Embedded
    private OauthId oauthId;

    @Builder
    public OauthMember(String nickname, String profileImageUrl, OauthId oauthId) {
        super(nickname, profileImageUrl);
        this.oauthId = oauthId;
    }
}
