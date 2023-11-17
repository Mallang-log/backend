package com.mallang.common.presentation

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.springframework.http.HttpMethod.*
import org.springframework.util.AntPathMatcher

@DisplayName("URI, HttpMethod, RequestParam 조건(UriAndMethodAndParamCondition) 은(는)")
internal class UriAndMethodAndParamConditionTest : StringSpec({

    val antPathMatcher = AntPathMatcher()
    val request = mockk<HttpServletRequest>()

    "URI HttpMethod RequestParam 이 모두 조건에 맞아야 매치된다" {
        // given
        val condition: UriAndMethodAndParamCondition = UriAndMethodAndParamCondition(
                uriPatterns = setOf("/test/**"),
                httpMethods = setOf(GET, POST),
                params = mapOf("name" to "mallang")
        )
        every { request.requestURI } answers { "/test/sample" }
        every { request.method } answers { POST.name() }
        every { request.getParameter("name") } answers { "mallang" }

        // when
        val match = condition.match(antPathMatcher, request)

        // then
        Assertions.assertThat(match).isTrue()
    }

    "RequestParam_은_아무_조건이_없다면_통과된다" {
        // given
        val condition = UriAndMethodAndParamCondition(
                uriPatterns = setOf("/test/**"),
                httpMethods = setOf(GET, POST),
                params = emptyMap()
        )
        every { request.requestURI } answers { "/test/sample" }
        every { request.method } answers { POST.name() }

        // when & then
        condition.match(antPathMatcher, request) shouldBe true
    }

    "URI가 조건에 맞지 않는 경우 매치 실패" {
        // given
        val condition = UriAndMethodAndParamCondition(
                uriPatterns = setOf("/test/**"),
                httpMethods = setOf(GET, POST),
                params = mapOf("name" to "mallang")
        )
        every { request.requestURI } answers { "/tst/sample" }
        every { request.method } answers { POST.name() }
        every { request.getParameter("name") } answers { "mallang" }

        // when & then
        condition.match(antPathMatcher, request) shouldBe false
    }

    "Method가 조건에 맞지 않는 경우 매치 실패" {
        // given
        val condition = UriAndMethodAndParamCondition(
                uriPatterns = setOf("/test/**"),
                httpMethods = setOf(GET, POST),
                params = mapOf("name" to "mallang")
        )
        every { request.requestURI } answers { "/test/sample" }
        every { request.method } answers { DELETE.name() }
        every { request.getParameter("name") } answers { "mallang" }

        // when & then
        condition.match(antPathMatcher, request) shouldBe false
    }

    "Param_이_조건에_맞지_않는_경우_매치_실패" {
        // given
        val condition = UriAndMethodAndParamCondition(
                uriPatterns = setOf("/test/**"),
                httpMethods = setOf(GET, POST),
                params = mapOf("name" to "mallang")
        )
        every { request.requestURI } answers { "/test/sample" }
        every { request.method } answers { GET.name() }
        every { request.getParameter("name") } answers { "wrong" }

        // when & then
        condition.match(antPathMatcher, request) shouldBe false
    }
})
