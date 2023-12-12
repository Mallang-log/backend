package com.mallang.reference.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.reference.application.command.CreateLabelCommand;
import com.mallang.reference.application.command.DeleteLabelCommand;
import com.mallang.reference.application.command.UpdateLabelAttributeCommand;
import com.mallang.reference.application.command.UpdateLabelHierarchyCommand;
import com.mallang.reference.domain.Label;
import com.mallang.reference.domain.LabelRepository;
import com.mallang.reference.domain.LabelValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class LabelService {

    private final LabelRepository labelRepository;
    private final MemberRepository memberRepository;
    private final LabelValidator labelValidator;

    public Long create(CreateLabelCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Label label = new Label(command.name(), member, command.colorCode());
        Label prev = labelRepository.getByIdIfIdNotNull(command.prevId());
        Label next = labelRepository.getByIdIfIdNotNull(command.nextId());
        label.create(prev, next, labelValidator);
        return labelRepository.save(label).getId();
    }

    public void updateHierarchy(UpdateLabelHierarchyCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Label label = labelRepository.getById(command.labelId());
        label.validateOwner(member);
        Label prev = labelRepository.getByIdIfIdNotNull(command.prevId());
        Label next = labelRepository.getByIdIfIdNotNull(command.nextId());
        label.updateHierarchy(prev, next);
    }

    public void updateAttribute(UpdateLabelAttributeCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Label label = labelRepository.getById(command.labelId());
        label.validateOwner(member);
        label.update(command.name(), command.colorCode());
    }

    public void delete(DeleteLabelCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Label label = labelRepository.getById(command.labelId());
        label.validateOwner(member);
        label.delete();
        labelRepository.delete(label);
    }
}
