package com.mallang.reference.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.reference.application.command.SaveReferenceLinkCommand;
import com.mallang.reference.application.command.UpdateReferenceLinkCommand;
import com.mallang.reference.domain.Label;
import com.mallang.reference.domain.LabelRepository;
import com.mallang.reference.domain.ReferenceLink;
import com.mallang.reference.domain.ReferenceLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ReferenceLinkService {

    private final LabelRepository labelRepository;
    private final MemberRepository memberRepository;
    private final ReferenceLinkRepository referenceLinkRepository;

    public Long save(SaveReferenceLinkCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Label label = labelRepository.getByIdIfIdNotNull(command.labelId());
        ReferenceLink referenceLink = command.toReferenceLink(member, label);
        return referenceLinkRepository.save(referenceLink).getId();
    }

    public void update(UpdateReferenceLinkCommand command) {
        Member member = memberRepository.getById(command.memberId());
        ReferenceLink link = referenceLinkRepository.getById(command.referenceLinkId());
        link.validateMember(member);
        Label label = labelRepository.getByIdIfIdNotNull(command.labelId());
        link.update(command.url(), command.title(), command.memo(), label);
    }

    public void delete(Long referenceLinkId, Long memberId) {
        Member member = memberRepository.getById(memberId);
        ReferenceLink link = referenceLinkRepository.getById(referenceLinkId);
        link.validateMember(member);
        referenceLinkRepository.delete(link);
    }
}
