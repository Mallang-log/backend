package com.mallang.post.presentation.request;

public record CreatePostFromDraftRequest(
        Long draftId,
        CreatePostRequest createPostRequest
) {
}
