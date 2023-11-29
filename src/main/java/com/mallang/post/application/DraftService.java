package com.mallang.post.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.category.domain.Category;
import com.mallang.category.domain.CategoryRepository;
import com.mallang.post.application.command.CreateDraftCommand;
import com.mallang.post.application.command.DeleteDraftCommand;
import com.mallang.post.application.command.UpdateDraftCommand;
import com.mallang.post.domain.PostIntro;
import com.mallang.post.domain.draft.Draft;
import com.mallang.post.domain.draft.DraftRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class DraftService {

    private final BlogRepository blogRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final DraftRepository draftRepository;

    public Long create(CreateDraftCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Blog blog = blogRepository.getByName(command.blogName());
        Category category = getCategoryByIdIfPresent(command.categoryId());
        Draft draft = command.toDraft(member, category, blog);
        return draftRepository.save(draft).getId();
    }

    public void update(UpdateDraftCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Draft draft = draftRepository.getById(command.draftId());
        Category category = getCategoryByIdIfPresent(command.categoryId());
        draft.validateWriter(member);
        draft.update(
                command.title(),
                command.bodyText(),
                command.postThumbnailImageName(),
                new PostIntro(command.intro()), // TODO gg
                category,
                command.tags()
        );
    }

    public void delete(DeleteDraftCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Draft draft = draftRepository.getById(command.draftId());
        draft.validateWriter(member);
        draftRepository.delete(draft);
    }

    private Category getCategoryByIdIfPresent(@Nullable Long id) {
        if (id == null) {
            return null;
        }
        return categoryRepository.getById(id);
    }
}
