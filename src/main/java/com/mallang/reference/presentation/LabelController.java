package com.mallang.reference.presentation;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.reference.application.LabelService;
import com.mallang.reference.application.command.DeleteLabelCommand;
import com.mallang.reference.presentation.request.CreateLabelRequest;
import com.mallang.reference.presentation.request.UpdateLabelAttributeRequest;
import com.mallang.reference.presentation.request.UpdateLabelHierarchyRequest;
import com.mallang.reference.query.LabelQueryService;
import com.mallang.reference.query.response.LabelListResponse;
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
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/labels")
@RestController
public class LabelController {

    private final LabelService labelService;
    private final LabelQueryService labelQueryService;

    @PostMapping
    public ResponseEntity<Void> create(
            @Auth Long memberId,
            @RequestBody CreateLabelRequest request
    ) {
        Long labelId = labelService.create(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/labels/" + labelId)).build();
    }

    @PutMapping("/{labelId}/hierarchy")
    public ResponseEntity<Void> updateHierarchy(
            @PathVariable(name = "labelId") Long labelId,
            @Auth Long memberId,
            @RequestBody UpdateLabelHierarchyRequest request
    ) {
        labelService.updateHierarchy(request.toCommand(labelId, memberId));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{labelId}/attributes")
    public ResponseEntity<Void> updateAttribute(
            @PathVariable(name = "labelId") Long labelId,
            @Auth Long memberId,
            @RequestBody UpdateLabelAttributeRequest request
    ) {
        labelService.updateAttribute(request.toCommand(labelId, memberId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{labelId}")
    public ResponseEntity<Void> delete(
            @PathVariable(name = "labelId") Long labelId,
            @Auth Long memberId
    ) {
        labelService.delete(new DeleteLabelCommand(memberId, labelId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<LabelListResponse>> findAllByMember(
            @Auth Long memberId
    ) {
        List<LabelListResponse> result = labelQueryService.findAllByMemberId(memberId);
        return ResponseEntity.ok(result);
    }
}
