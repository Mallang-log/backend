package com.mallang.reference.presentation;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.reference.application.FetchUrlTitleMetaInfoService;
import com.mallang.reference.application.ReferenceLinkService;
import com.mallang.reference.application.command.SaveReferenceLinkCommand;
import com.mallang.reference.application.command.UpdateReferenceLinkCommand;
import com.mallang.reference.presentation.request.SaveReferenceLinkRequest;
import com.mallang.reference.presentation.request.UpdateReferenceLinkRequest;
import com.mallang.reference.query.ReferenceLinkQueryService;
import com.mallang.reference.query.repository.ReferenceLinkSearchDao.ReferenceLinkSearchDaoCond;
import com.mallang.reference.query.response.ReferenceLinkSearchResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/reference-links")
@RestController
public class ReferenceLinkController {

    private final ReferenceLinkService referenceLinkService;
    private final ReferenceLinkQueryService referenceLinkQueryService;
    private final FetchUrlTitleMetaInfoService fetchReferenceLinkTitleService;

    @GetMapping("/title-info")
    public ResponseEntity<String> fetchTitleInfo(
            @RequestParam("url") String url
    ) {
        return ResponseEntity.ok(fetchReferenceLinkTitleService.fetchTitleMetaInfo(url));
    }

    @PostMapping
    public ResponseEntity<Void> save(
            @Auth Long memberId,
            @Valid @RequestBody SaveReferenceLinkRequest request
    ) {
        SaveReferenceLinkCommand command = request.toCommand(memberId);
        Long referenceLinkId = referenceLinkService.save(command);
        return ResponseEntity.created(URI.create("/reference-title/" + referenceLinkId)).build();
    }

    @PutMapping("/{referenceLinkId}")
    public ResponseEntity<Void> update(
            @Auth Long memberId,
            @PathVariable(name = "referenceLinkId") Long referenceLinkId,
            @Valid @RequestBody UpdateReferenceLinkRequest request
    ) {
        UpdateReferenceLinkCommand command = request.toCommand(referenceLinkId, memberId);
        referenceLinkService.update(command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{referenceLinkId}")
    public ResponseEntity<Void> delete(
            @Auth Long memberId,
            @PathVariable(name = "referenceLinkId") Long referenceLinkId
    ) {
        referenceLinkService.delete(referenceLinkId, memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkExistsUrl(
            @Auth Long memberId,
            @RequestParam("url") String url
    ) {
        boolean isExists = referenceLinkQueryService.existsReferenceLinkByUrl(memberId, url.strip());
        return ResponseEntity.ok(isExists);
    }

    @GetMapping
    public ResponseEntity<List<ReferenceLinkSearchResponse>> search(
            @Auth Long memberId,
            @ModelAttribute ReferenceLinkSearchDaoCond cond
    ) {
        List<ReferenceLinkSearchResponse> result = referenceLinkQueryService.search(memberId, cond);
        return ResponseEntity.ok(result);
    }
}
