package com.beb.backend.service;

import com.beb.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 이메일이 DB의 회원 테이블에 존재하는지 반환
     * @param email (String) 확인할 이메일 주소
     * @return (boolean) 이미 존재하는 이메일이면 true, 아니면 false
     */
    public boolean isEmailDuplicated(String email) {
        return memberRepository.existsByEmail(email);
    }
}
