package com.example.manage.service;

import com.example.manage.domain.Member;
import com.example.manage.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
