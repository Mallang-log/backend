package com.mallang.post.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.post.application.command.CreateStarGroupCommand;
import com.mallang.post.application.command.DeleteStarGroupCommand;
import com.mallang.post.application.command.UpdateStarGroupHierarchyCommand;
import com.mallang.post.application.command.UpdateStarGroupNameCommand;
import com.mallang.post.domain.star.PostStarRepository;
import com.mallang.post.domain.star.StarGroup;
import com.mallang.post.domain.star.StarGroupRepository;
import com.mallang.post.domain.star.StarGroupValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class StarGroupService {

    private final MemberRepository memberRepository;
    private final PostStarRepository postStarRepository;
    private final StarGroupRepository starGroupRepository;
    private final StarGroupValidator starGroupValidator;

    public Long create(CreateStarGroupCommand command) {
        Member member = memberRepository.getById(command.memberId());
        StarGroup group = new StarGroup(command.name(), member);
        StarGroup parent = starGroupRepository.getByIdIfIdNotNull(command.parentId());
        StarGroup prev = starGroupRepository.getByIdIfIdNotNull(command.prevId());
        StarGroup next = starGroupRepository.getByIdIfIdNotNull(command.nextId());
        group.create(parent, prev, next, starGroupValidator);
        return starGroupRepository.save(group).getId();
    }

    public void updateHierarchy(UpdateStarGroupHierarchyCommand command) {
        Member member = memberRepository.getById(command.memberId());
        StarGroup target = starGroupRepository.getById(command.groupId());
        target.validateOwner(member);
        StarGroup parent = starGroupRepository.getByIdIfIdNotNull(command.parentId());
        StarGroup prev = starGroupRepository.getByIdIfIdNotNull(command.prevId());
        StarGroup next = starGroupRepository.getByIdIfIdNotNull(command.nextId());
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
        StarGroup group = starGroupRepository.getById(command.groupId());
        group.validateOwner(member);
        group.delete();
        postStarRepository.findAllByStarGroup(group)
                .forEach(it -> it.updateGroup(null));
        starGroupRepository.delete(group);
    }
}
