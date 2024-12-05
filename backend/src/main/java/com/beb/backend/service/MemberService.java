package com.beb.backend.service;

import com.beb.backend.auth.BebAuthenticationProvider;
import com.beb.backend.auth.JwtGenerator;
import com.beb.backend.domain.Member;
import com.beb.backend.dto.LoginRequestDto;
import com.beb.backend.dto.TokenResponseDto;
import com.beb.backend.dto.SignUpRequestDto;
import com.beb.backend.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;

    /**
     * 입력된 회원 정보로 회원을 생성하고 액세스 토큰과 리프레시 토큰 반환
     * @param request (MemberSignUpRequestDto)
     * @return (TokenResponseDto)
     */
    public TokenResponseDto signUp(SignUpRequestDto request) {
        if (isEmailDuplicated(request.email())) throw new IllegalArgumentException("Email already in use");
        if (isNicknameDuplicated(request.nickname())) throw new IllegalArgumentException("Nickname already in use");

        // TODO: 비밀번호 형식 검증
        String encodedPassword = passwordEncoder.encode(request.password());
        // 회원 생성
        Member member = Member.builder()
                .email(request.email())
                .password(encodedPassword)
                .nickname(request.nickname())
                .age(request.age())
                .gender(request.gender())
                .profileImgPath(request.profileImgPath()).build();
        // 회원 저장
        Member savedMember = memberRepository.save(member);
        // 토큰 발급. TODO: Refresh token 저장
        String accessToken = jwtGenerator.createAccessToken(savedMember.getEmail());
        String refreshToken = jwtGenerator.createRefreshToken(savedMember.getEmail());
        return new TokenResponseDto(accessToken, refreshToken);
    }

    /**
     * 이메일이 DB의 회원 테이블에 존재하는지 반환
     * @param email (String) 확인할 이메일 주소
     * @return (boolean) 이미 존재하는 이메일이면 true, 아니면 false
     */
    public boolean isEmailDuplicated(String email) {
        return memberRepository.existsByEmail(email);
    }

    /**
     * 닉네임이 DB의 회원 테이블에 존재하는지 반환
     * @param nickname (String) 확인할 닉네임
     * @return (boolean) 이미 존재하는 닉네임이면 true, 아니면 false
     */
    public boolean isNicknameDuplicated(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    /**
     * 입력된 이메일(로그인 ID), 비밀번호로 인증. 성공 시 액세스 토큰, 리프레시 토큰 반환
     * Spring Security의 AuthenticationManager 이용
     * @param request (LoginRequestDto) "email", "password"
     * @return (TokenResponseDto)
     *
     * @see com.beb.backend.config.SecurityConfig
     * @see BebAuthenticationProvider
     * @see JwtGenerator
     */
    public TokenResponseDto login(LoginRequestDto request) {
        // 1. 인증 객체 생성 & Spring Security 인증 작업 수동 호출
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.email(), request.password())
        );

        // 인증 실패하면 예외 발생시키게 되어 있어, authentication이 null로 나오는 경우는 없음
        // 2. 인증 완료 시 JWT 생성
        String accessToken = jwtGenerator.createAccessToken(authentication.getName());
        String refreshToken = jwtGenerator.createRefreshToken(authentication.getName());
        return new TokenResponseDto(accessToken, refreshToken);
    }
}
