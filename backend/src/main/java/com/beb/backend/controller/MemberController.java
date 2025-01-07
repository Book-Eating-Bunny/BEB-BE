package com.beb.backend.controller;

import com.beb.backend.common.ValidationRegexConstants;
import com.beb.backend.dto.*;
import com.beb.backend.exception.MemberException;
import com.beb.backend.exception.MemberExceptionInfo;
import com.beb.backend.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Validated
public class MemberController {

    private final MemberService memberService;

    /**
     * 입력된 정보로 회원을 생성하고 액세스 토큰과 리프레시 토큰을 반환
     * @param request (MemberSignUpRequestDto)
     */
    @PostMapping("/signup")
    public ResponseEntity<BaseResponseDto<TokenResponseDto>> signUp(
            @RequestBody @Valid SignUpRequestDto request) {
        TokenResponseDto tokenResponse = memberService.signUp(request);
        BaseResponseDto<TokenResponseDto> response = BaseResponseDto.success(
                tokenResponse, new BaseResponseDto.Meta("회원 가입 성공"));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 사용할 수 있는 이메일인지 확인하여 응답으로 반환
     * @param email (String) 확인할 이메일 주소
     */
    @GetMapping("/email-availability")
    public ResponseEntity<BaseResponseDto<AvailabilityResponseDto>> checkEmailAvailability(
            @RequestParam @NotBlank String email) {
        // email 형식 검사
        if (!email.matches(ValidationRegexConstants.EMAIL_REGEX)) {
            throw new MemberException(MemberExceptionInfo.EMAIL_NOT_VALID);
        }
        // email 중복 검사
        BaseResponseDto<AvailabilityResponseDto> response;
        AvailabilityResponseDto availabilityResponseDto = new AvailabilityResponseDto(!memberService.isEmailDuplicated(email));

        if (availabilityResponseDto.isAvailable()) {
            response = BaseResponseDto.success(availabilityResponseDto, new BaseResponseDto.Meta("사용 가능한 이메일"));
        } else {
            response = BaseResponseDto.success(availabilityResponseDto, new BaseResponseDto.Meta("이미 존재하는 이메일"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 사용할 수 있는 닉네임인지 확인하여 응답으로 반환
     * @param nickname (String) 확인할 닉네임
     */
    @GetMapping("/nickname-availability")
    public ResponseEntity<BaseResponseDto<AvailabilityResponseDto>> checkNicknameAvailability(
            @RequestParam @NotBlank String nickname) {
        // nickname 형식 검사
        if (!nickname.matches(ValidationRegexConstants.NICKNAME_REGEX)) {
            throw new MemberException(MemberExceptionInfo.NICKNAME_NOT_VALID);
        }
        // nickname 중복 검사
        BaseResponseDto<AvailabilityResponseDto> response;
        AvailabilityResponseDto availabilityResponseDto = new AvailabilityResponseDto(!memberService.isNicknameDuplicated(nickname));

        if (availabilityResponseDto.isAvailable()) {
            response = BaseResponseDto.success(availabilityResponseDto, new BaseResponseDto.Meta("사용 가능한 닉네임"));
        } else {
            response = BaseResponseDto.success(availabilityResponseDto, new BaseResponseDto.Meta("이미 존재하는 닉네임"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 입력된 이메일(로그인 ID)과 비밀번호를 확인하여 인증 성공 시 액세스 토큰, 리프레시 토큰 반환
     * @param loginRequest (LoginRequestDto) "email", "password"
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponseDto<TokenResponseDto>> login(@RequestBody @Valid LoginRequestDto loginRequest) {
        TokenResponseDto loginResponse = memberService.login(loginRequest);
        BaseResponseDto<TokenResponseDto> response = BaseResponseDto.success(
                loginResponse, new BaseResponseDto.Meta("로그인 성공")
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 헤더에 입력된 리프레시 토큰의 유효성을 확인하여 액세스 토큰과 리프레시 토큰을 재발급
     * @param refreshToken (String) 리프레시 토큰
     */
    @PostMapping("/reissue")
    public ResponseEntity<BaseResponseDto<TokenResponseDto>> reissueJwt(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String refreshToken) {

        TokenResponseDto tokenResponse = memberService.reissueJwt(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.success(
                tokenResponse, new BaseResponseDto.Meta("토큰 재발급 성공")
        ));
    }

    /**
     * 현재 인증된 사용자를 로그아웃 처리
     * @param authorization (String) 헤더에 저장된 Authorization 값. "Bearer {액세스 토큰}" 이어야 함.
     */
    @PostMapping("/logout")
    public ResponseEntity<BaseResponseDto<Void>> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization) {
        String accessToken = authorization.split(" ")[1];
        memberService.logout(accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.emptySuccess("로그아웃 성공"));
    }

    /**
     * 현재 인증된 사용자의 프로필 정보(비밀번호 제외) 반환
     */
    @GetMapping("/me")
    public ResponseEntity<BaseResponseDto<FullProfileDto>> getCurrentUserProfile() {
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.success(
                memberService.getCurrentUserProfile(), new BaseResponseDto.Meta("프로필 조회 성공")
        ));
    }

    /**
     * 입력된 userId와 일치하는 사용자의 프로필 정보 반환
     * @param userId (Long) 프로필을 조회할 사용자 PK
     */
    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponseDto<ProfileResponseDto>> getUserProfile(@PathVariable @Min(value = 1) Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.success(
                memberService.getUserProfile(userId), new BaseResponseDto.Meta("프로필 조회 성공")
        ));
    }

    /**
     * 현재 인증된 사용자의 프로필 정보를 입력 받은 정보로 수정. 입력된 항목만 수정한다.
     * @param request (UpdateProfileRequestDto)
     */
    @PutMapping("/me")
    public ResponseEntity<BaseResponseDto<Void>> updateUserProfile(@RequestBody @Valid UpdateProfileRequestDto request) {
        memberService.updateUserProfile(request);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.emptySuccess("프로필 수정 성공"));
    }
}
