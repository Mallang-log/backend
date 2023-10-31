package com.mallang.blog.domain;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.common.domain.CommonDomainModel;
import com.mallang.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Blog extends CommonDomainModel {

    @Column(unique = true)
    private String name;

    @Embedded
    private BlogDomain domain;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
