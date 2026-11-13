
package com.example.library.service;

import com.example.library.dto.request.MemberRequest;
import com.example.library.dto.response.MemberResponse;
import com.example.library.entity.Member;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.LoanRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public MemberService(MemberRepository memberRepository,
                         LoanRepository loanRepository) {
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberById(Long id) {
        return toResponse(findMember(id));
    }

    @Transactional
    public MemberResponse createMember(MemberRequest request) {
        if (memberRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already exists");
        }

        Member member = new Member();
        applyRequest(member, request);

        return toResponse(memberRepository.save(member));
    }

    @Transactional
    public MemberResponse updateMember(Long id, MemberRequest request) {
        Member member = findMember(id);

        memberRepository.findByEmailIgnoreCase(request.email())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT, "Email already exists");
                    }
                });

        applyRequest(member, request);
        return toResponse(memberRepository.save(member));
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = findMember(id);

        if (!loanRepository.findByMemberId(id).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete member with loan history");
        }

        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> searchByName(String name) {
        return memberRepository
                .findByFirstNameContainingIgnoreCase(name)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getMembersWithActiveLoans() {
        return memberRepository.findMembersWithActiveLoans()
                .stream().map(this::toResponse).toList();
    }

    private Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Member not found"));
    }

    private void applyRequest(Member member, MemberRequest request) {
        member.setFirstName(request.firstName());
        member.setLastName(request.lastName());
        member.setEmail(request.email());
        member.setPhone(request.phone());
    }

    private MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getPhone()
        );
    }
}
