package com.mallang.post.presentation;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.post.application.StarGroupService;
import com.mallang.post.application.command.DeleteStarGroupCommand;
import com.mallang.post.presentation.request.CreateStarGroupRequest;
import com.mallang.post.presentation.request.UpdateStarGroupHierarchyRequest;
import com.mallang.post.presentation.request.UpdateStarGroupNameRequest;
import com.mallang.post.query.StarGroupQueryService;
import com.mallang.post.query.response.StarGroupListResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/star-groups")
@RestController
public class StarGroupController {

    private final StarGroupService starGroupService;
    private final StarGroupQueryService starGroupQueryService;

    @PostMapping
    public ResponseEntity<Void> create(
            @Auth Long memberId,
            @RequestBody CreateStarGroupRequest request
    ) {
        Long groupId = starGroupService.create(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/star-groups/" + groupId)).build();
    }

    @PutMapping("/{groupId}/hierarchy")
    public ResponseEntity<Void> updateHierarchy(
            @PathVariable(name = "groupId") Long groupId,
            @Auth Long memberId,
            @RequestBody UpdateStarGroupHierarchyRequest request
    ) {
        starGroupService.updateHierarchy(request.toCommand(groupId, memberId));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{groupId}/name")
    public ResponseEntity<Void> updateName(
            @PathVariable(name = "groupId") Long groupId,
            @Auth Long memberId,
            @RequestBody UpdateStarGroupNameRequest request
    ) {
        starGroupService.updateName(request.toCommand(groupId, memberId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> delete(
            @PathVariable(name = "groupId") Long groupId,
            @Auth Long memberId
    ) {
        starGroupService.delete(new DeleteStarGroupCommand(memberId, groupId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<StarGroupListResponse>> findAllByMember(
            @RequestParam(name = "memberId", required = true) Long memberId
    ) {
        List<StarGroupListResponse> result = starGroupQueryService.findAllByMember(memberId);
        return ResponseEntity.ok(result);
    }
}
