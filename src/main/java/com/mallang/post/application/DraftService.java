package com.mallang.post.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.post.application.command.CreateDraftCommand;
import com.mallang.post.application.command.DeleteDraftCommand;
import com.mallang.post.application.command.UpdateDraftCommand;
import com.mallang.post.domain.PostCategory;
import com.mallang.post.domain.PostCategoryRepository;
import com.mallang.post.domain.draft.Draft;
import com.mallang.post.domain.draft.DraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class DraftService {

    private final BlogRepository blogRepository;
    private final MemberRepository memberRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final DraftRepository draftRepository;

    public Long create(CreateDraftCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Blog blog = blogRepository.getByName(command.blogName());
        PostCategory postCategory = postCategoryRepository.getByIdIfIdNotNull(command.categoryId());
        Draft draft = command.toDraft(member, blog, postCategory);
        return draftRepository.save(draft).getId();
    }

    public void update(UpdateDraftCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Draft draft = draftRepository.getById(command.draftId());
        PostCategory postCategory = postCategoryRepository.getByIdIfIdNotNull(command.categoryId());
        draft.validateWriter(member);
        draft.update(
                command.title(),
                command.bodyText(),
                postCategory,
                command.tags()
        );
    }

    public void delete(DeleteDraftCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Draft draft = draftRepository.getById(command.draftId());
        draft.validateWriter(member);
        draftRepository.delete(draft);
    }
}
