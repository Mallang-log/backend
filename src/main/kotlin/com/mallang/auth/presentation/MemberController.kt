package com.mallang.auth.presentation

import com.mallang.auth.presentation.support.Auth
import com.mallang.auth.query.MemberQueryService
import com.mallang.auth.query.dao.model.MemberProfileQueryModel
import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
class MemberController(
        private val memberQueryService: MemberQueryService
) {

    @GetMapping("/{id}")
    fun findProfile(
            @PathVariable("id") memberId: Long? // TODO non null
    ): ResponseEntity<MemberProfileQueryModel> {
        return ResponseEntity.ok(memberQueryService.findProfile(memberId!!))
    }

    @GetMapping("/my")
    fun findMyProfile(
            @Auth memberId: Long?
    ): ResponseEntity<MemberProfileQueryModel> {
        return ResponseEntity.ok(memberQueryService.findProfile(memberId!!))
    }
}
