package com.example.manage.service;

import com.example.manage.domain.Member;
import com.example.manage.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void createMember(
            Integer participantNo,
            String group,
            String loginId,
            String rawPassword
    ) {

        if (memberRepository.findByLoginId(loginId).isPresent()) {
            return;
        }

        if (memberRepository.findByParticipantNo(participantNo).isPresent()) {
            return;
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        Member member = new Member(participantNo, group, loginId, encodedPassword);

        memberRepository.save(member);
    }

    public Member login(String loginId, String rawPassword) {

        Member member = memberRepository.findByLoginId(loginId).orElse(null);

        if (member == null) {
            return null;
        }

        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            return null;
        }

        return member;
    }

    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }

    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }
}
