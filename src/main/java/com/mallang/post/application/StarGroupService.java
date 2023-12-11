package com.mallang.post.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.post.application.command.CreateStarGroupCommand;
import com.mallang.post.application.command.DeleteStarGroupCommand;
import com.mallang.post.application.command.UpdateStarGroupHierarchyCommand;
import com.mallang.post.application.command.UpdateStarGroupNameCommand;
import com.mallang.post.domain.star.StarGroup;
import com.mallang.post.domain.star.StarGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class StarGroupService {

    private final MemberRepository memberRepository;
    private final StarGroupRepository starGroupRepository;

    public Long create(CreateStarGroupCommand command) {
        Member member = memberRepository.getById(command.memberId());
        StarGroup group = new StarGroup(command.name(), member);
        updateHierarchy(group, command.parentId(), command.prevId(), command.nextId());
        return starGroupRepository.save(group).getId();
    }

    public void updateHierarchy(UpdateStarGroupHierarchyCommand command) {
        Member member = memberRepository.getById(command.memberId());
        StarGroup target = starGroupRepository.getById(command.groupId());
        target.validateOwner(member);
        updateHierarchy(target, command.parentId(), command.prevId(), command.nextId());
    }

    private void updateHierarchy(StarGroup target, Long parentId, Long prevId, Long nextId) {
        StarGroup parent = starGroupRepository.getByIdIfIdNotNull(parentId);
        StarGroup prev = starGroupRepository.getByIdIfIdNotNull(prevId);
        StarGroup next = starGroupRepository.getByIdIfIdNotNull(nextId);
        target.updateHierarchy(parent, prev, next);
    }

    public void updateName(UpdateStarGroupNameCommand command) {
        Member member = memberRepository.getById(command.memberId());
        StarGroup group = starGroupRepository.getById(command.groupId());
        group.validateOwner(member);
        group.updateName(command.name());
    }

    public void delete(DeleteStarGroupCommand command) {
        Member member = memberRepository.getById(command.memberId());
        StarGroup group = starGroupRepository.getById(command.categoryId());
        group.validateOwner(member);
        group.delete();
        starGroupRepository.delete(group);
    }
}