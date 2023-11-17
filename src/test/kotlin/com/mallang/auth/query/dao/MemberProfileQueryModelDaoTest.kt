package com.mallang.auth.query.dao

import com.mallang.auth.domain.Member
import com.mallang.auth.domain.MemberRepository
import com.mallang.auth.domain.OauthId
import com.mallang.auth.domain.OauthServerType.GITHUB
import com.mallang.common.ServiceTest
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName

@DisplayName("회원 프로필 조회 DAO(MemberProfileDataDao) 은(는)")
@ServiceTest
internal class MemberProfileQueryModelDaoTest(
        val memberRepository: MemberRepository,
        val memberProfileDataDao: MemberProfileDataDao
) : StringSpec({

    "회원정보를_조회한다" {
        // given
        val member = Member.builder()  // TODO 변경
                .nickname("mallang")
                .profileImageUrl("profile")
                .oauthId(OauthId("test", GITHUB))
                .build()
        val saved = memberRepository.save(member)

        // when
        val (id, nickname, profileImageUrl) = memberProfileDataDao.find(saved.id)

        // then
        id shouldBe saved.id
        profileImageUrl shouldBe "profile"
        nickname shouldBe "mallang"
    }
})
