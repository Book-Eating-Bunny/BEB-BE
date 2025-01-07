package com.beb.backend.service;

import com.beb.backend.auth.BebAuthenticationProvider;
import com.beb.backend.auth.JwtUtils;
import com.beb.backend.domain.Member;
import com.beb.backend.domain.RefreshToken;
import com.beb.backend.dto.*;
import com.beb.backend.exception.MemberException;
import com.beb.backend.exception.MemberExceptionInfo;
import com.beb.backend.repository.MemberRepository;
import com.beb.backend.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 입력된 회원 정보로 회원을 생성하고 액세스 토큰과 리프레시 토큰 반환
     * @param request (MemberSignUpRequestDto)
     * @return (TokenResponseDto)
     */
    @Transactional
    public TokenResponseDto signUp(SignUpRequestDto request) {
        if (isEmailDuplicated(request.email())) throw new MemberException(MemberExceptionInfo.DUPLICATE_EMAIL);
        if (isNicknameDuplicated(request.nickname())) throw new MemberException(MemberExceptionInfo.DUPLICATE_NICKNAME);

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
        // 토큰 발급
        String accessToken = jwtUtils.createAccessToken(savedMember.getEmail());
        String refreshToken = jwtUtils.createRefreshToken(savedMember.getEmail());
        refreshTokenRepository.save(new RefreshToken(savedMember.getEmail(), refreshToken));
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
     * @see JwtUtils
     */
    @Transactional
    public TokenResponseDto login(LoginRequestDto request) {
        // 1. 인증 객체 생성 & Spring Security 인증 작업 수동 호출
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.email(), request.password())
        );

        // 2. 인증 완료 시 JWT 생성
        String accessToken = jwtUtils.createAccessToken(authentication.getName());
        String refreshToken = jwtUtils.createRefreshToken(authentication.getName());

        // 3. 리프레시 토큰 업데이트
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUsername(authentication.getName());
        existingToken.ifPresentOrElse(
                token -> token.setToken(refreshToken),
                () -> refreshTokenRepository.save(new RefreshToken(authentication.getName(), refreshToken)));
        return new TokenResponseDto(accessToken, refreshToken);
    }

    /**
     * 입력된 리프레시 토큰의 유효성을 확인하여 액세스 토큰과 리프레시 토큰을 재발급
     * @param refreshToken (String)
     * @return (TokenResponseDto)
     */
    @Transactional
    public TokenResponseDto reissueJwt(String refreshToken) {
        // 1. 리프레시 토큰 검증
        if (!jwtUtils.validateToken(refreshToken)) throw new IllegalArgumentException("Invalid refresh token");

        String username = jwtUtils.getUsernameFromToken(refreshToken);
        RefreshToken storedToken = refreshTokenRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("로그아웃된 사용자"));

        if (!refreshToken.equals(storedToken.getToken())) throw new RuntimeException("토큰의 유저 정보 불일치");

        // 2. 토큰 재발급
        String newAccessToken = jwtUtils.createAccessToken(username);
        String newRefreshToken = jwtUtils.createRefreshToken(username);
        // 3. 리프레시 토큰 업데이트
        storedToken.setToken(newAccessToken);
        return new TokenResponseDto(newAccessToken, newRefreshToken);
    }

    /**
     * 입력된 액세스 토큰으로부터 현재 인증된 사용자를 확인하여 그 사용자의 리프레시 토큰을 DB에서 삭제
     * @param accessToken (String) 액세스 토큰
     */
    @Transactional
    public void logout(String accessToken) {
        String username = jwtUtils.getUsernameFromToken(accessToken);
        refreshTokenRepository.deleteByUsername(username);
        // TODO: 로그아웃한 사용자의 액세스 토큰 저장 (추후 Redis 연결 시 추가)
    }

    private FullProfileDto mapToFullProfileDto(Member member) {
        return new FullProfileDto(
                member.getEmail(),
                member.getNickname(),
                member.getAge(),
                member.getGender(),
                member.getProfileImgPath()
        );
    }

    /**
     * 현재 인증된 유저의 프로필 정보 반환
     * 비밀번호를 제외한 모든 정보(이메일, 닉네임, 나이, 성별, 프로필 사진)를 반환한다.
     * @return (FullProfileDto)
     */
    public FullProfileDto getCurrentUserProfile() {
        Member member = getCurrentMember()
                .orElseThrow(() -> new MemberException(MemberExceptionInfo.MEMBER_NOT_FOUND));
        return mapToFullProfileDto(member);
    }

    /**
     * 입력된 userId와 일치하는 유저의 프로필 정보를 반환한다.
     * 현재 인증된 유저와 같을 경우 비밀번호를 제외한 모든 정보를 반환하고,
     * 그렇지 않을 경우 닉네임과 프로필 사진 정보만 반환한다.
     * @param userId (Long)
     * @return (ProfileResponseDto)
     */
    public ProfileResponseDto getUserProfile(Long userId) {
        Optional<Member> currentMember = getCurrentMember();
        if (currentMember.isPresent() && currentMember.get().getId().equals(userId)) {
            return mapToFullProfileDto(currentMember.get());
        }
        return memberRepository.findById(userId)
                .map(member -> new PublicProfileDto(
                        member.getNickname(),
                        member.getProfileImgPath()
                ))
                .orElseThrow(() -> new MemberException(MemberExceptionInfo.MEMBER_NOT_FOUND));
    }

    /**
     * 현재 인증된 사용자 프로필을 입력된 정보로 수정 (비밀번호, 닉네임, 나이, 성별, 프로필 사진 수정 가능)
     * @param request (UpdateProfileRequestDto)
     */
    @Transactional
    public void updateUserProfile(UpdateProfileRequestDto request) {
        Member member = getCurrentMember().orElseThrow(() -> new MemberException(MemberExceptionInfo.MEMBER_NOT_FOUND));

        if (request.nickname() != null && !request.nickname().equals(member.getNickname())) {
            if (isNicknameDuplicated(request.nickname())) {
                throw new MemberException(MemberExceptionInfo.DUPLICATE_NICKNAME);
            }
            member.setNickname(request.nickname());
        }
        if (request.password() != null) member.setPassword(passwordEncoder.encode(request.password()));
        if (request.age() != null) member.setAge(request.age());
        if (request.gender() != null) member.setGender(request.gender());
        if (request.profileImgPath() != null) member.setProfileImgPath(request.profileImgPath());
    }

    public Optional<Member> getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return memberRepository.findByEmail(authentication.getName());
    }
}
