package com.surest_member_managemant.service;

import com.surest_member_managemant.dto.MemberRequest;
import com.surest_member_managemant.dto.MemberResponse;
import com.surest_member_managemant.entity.Member;
import com.surest_member_managemant.exception.NotFoundException;
import com.surest_member_managemant.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.UUID;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberResponse createMember(MemberRequest req) {
        if (memberRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }


        Member member = Member.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .dateOfBirth(req.getDateOfBirth())
                .email(req.getEmail())
                .build();


        Member saved = memberRepository.saveAndFlush(member);


        return MemberResponse.builder()
                .id(saved.getId())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .dateOfBirth(saved.getDateOfBirth())
                .email(saved.getEmail())
//                .createdAt(saved.getCreatedAt())
//                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    public Page<MemberResponse> listMembers(String firstName, String lastName, Pageable pageable) {
        Page<Member> page;

        if (firstName != null && lastName != null) {
            page = memberRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName, pageable);
        } else if (firstName != null) {
            page = memberRepository.findByFirstNameContainingIgnoreCase(firstName, pageable);
        } else if (lastName != null) {
            page = memberRepository.findByLastNameContainingIgnoreCase(lastName, pageable);
        } else {
            page = memberRepository.findAll(pageable);
        }

        return page.map(member -> MemberResponse.builder()
                .id(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .dateOfBirth(member.getDateOfBirth())
                .email(member.getEmail())
//                .createdAt(member.getCreatedAt())
//                .updatedAt(member.getUpdatedAt())
                .build());
    }

    @Cacheable(value = "members", key = "#id")
    public MemberResponse getById(UUID id) {
        Member m = memberRepository.findById(id).orElseThrow(() -> new NotFoundException("Member not found"));
        return MemberResponse.builder()
                .id(m.getId())
                .firstName(m.getFirstName())
                .lastName(m.getLastName())
                .email(m.getEmail())
                .dateOfBirth(m.getDateOfBirth())
//                .createdAt(m.getCreatedAt())
//                .updatedAt(m.getUpdatedAt())
                .build();
    }

    @Transactional
    @CachePut(value = "members", key = "#id")
    public MemberResponse updateMember(UUID id, MemberRequest req) {
        Member existing = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Member not found"));

        String existingEmail = existing.getEmail();
        String newEmail = req.getEmail();

        if (!Objects.equals(existingEmail, newEmail)
                && memberRepository.existsByEmailAndIdNot(newEmail, id)) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Update fields
        existing.setFirstName(req.getFirstName());
        existing.setLastName(req.getLastName());
        existing.setDateOfBirth(req.getDateOfBirth());
        existing.setEmail(newEmail);

        Member saved = memberRepository.save(existing);

        // Manual mapping (no fromEntity)
        return MemberResponse.builder()
                .id(saved.getId())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .dateOfBirth(saved.getDateOfBirth())
                .email(saved.getEmail())
//                .createdAt(saved.getCreatedAt())
//                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    @Transactional
    @CacheEvict(value = "members", key = "#id")
    public void delete(UUID id) {
        if (!memberRepository.existsById(id)) throw new NotFoundException("Member not found");
        memberRepository.deleteById(id);
    }


}

