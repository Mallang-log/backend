package com.mallang.member.domain;

import static lombok.AccessLevel.PROTECTED;

import com.mallang.common.domain.CommonDomainModel;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
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
}
