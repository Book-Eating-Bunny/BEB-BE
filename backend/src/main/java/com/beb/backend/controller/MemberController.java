package com.beb.backend.controller;

import com.beb.backend.dto.*;
import com.beb.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class MemberController {

    private final MemberService memberService;

    /**
     * 입력된 정보로 회원을 생성하고 액세스 토큰과 리프레시 토큰을 반환
     * @param request (MemberSignUpRequestDto)
     */
    @PostMapping("/signup")
    public ResponseEntity<BaseResponseDto<TokenResponseDto>> signUp(
            @RequestBody SignUpRequestDto request) {
        try {
            TokenResponseDto tokenResponse = memberService.signUp(request);
            BaseResponseDto<TokenResponseDto> response = BaseResponseDto.success(
                    tokenResponse, new BaseResponseDto.Meta("회원 가입 성공"));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            BaseResponseDto<TokenResponseDto> response = BaseResponseDto.fail("잘못된 요청");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            BaseResponseDto<TokenResponseDto> response = BaseResponseDto.fail(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        String str = "test success";
        return ResponseEntity.status(HttpStatus.OK).body(str);
    }

    /**
     * 사용할 수 있는 이메일인지 확인하여 응답으로 반환
     * @param email (String) 확인할 이메일 주소
     */
    @GetMapping("/email-availability")
    public ResponseEntity<BaseResponseDto<AvailabilityResponseDto>> checkEmailAvailability(@RequestParam String email) {
        try {
            // TODO: email 형식 검사
            // email 중복 검사
            BaseResponseDto<AvailabilityResponseDto> response;
            AvailabilityResponseDto availabilityResponseDto = new AvailabilityResponseDto(!memberService.isEmailDuplicated(email));
            if (availabilityResponseDto.isAvailable()) {
                response = BaseResponseDto.success(availabilityResponseDto, new BaseResponseDto.Meta("사용 가능한 이메일"));
            } else {
                response = BaseResponseDto.success(availabilityResponseDto, new BaseResponseDto.Meta("이미 존재하는 이메일"));
            }
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            BaseResponseDto<AvailabilityResponseDto> response = BaseResponseDto.fail("잘못된 이메일 형식");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            BaseResponseDto<AvailabilityResponseDto> response = BaseResponseDto.fail(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 사용할 수 있는 닉네임인지 확인하여 응답으로 반환
     * @param nickname (String) 확인할 닉네임
     */
    @GetMapping("/nickname-availability")
    public ResponseEntity<BaseResponseDto<AvailabilityResponseDto>> checkNicknameAvailability(@RequestParam String nickname) {
        try {
            // TODO: nickname 형식 검사
            // nickname 중복 검사
            BaseResponseDto<AvailabilityResponseDto> response;
            AvailabilityResponseDto availabilityResponseDto = new AvailabilityResponseDto(!memberService.isNicknameDuplicated(nickname));
            if (availabilityResponseDto.isAvailable()) {
                response = BaseResponseDto.success(availabilityResponseDto, new BaseResponseDto.Meta("사용 가능한 닉네임"));
            } else {
                response = BaseResponseDto.success(availabilityResponseDto, new BaseResponseDto.Meta("이미 존재하는 닉네임"));
            }
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            BaseResponseDto<AvailabilityResponseDto> response = BaseResponseDto.fail("잘못된 닉네임 형식");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            BaseResponseDto<AvailabilityResponseDto> response = BaseResponseDto.fail(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 입력된 이메일(로그인 ID)과 비밀번호를 확인하여 인증 성공 시 액세스 토큰, 리프레시 토큰 반환
     * @param loginRequest (LoginRequestDto) "email", "password"
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponseDto<TokenResponseDto>> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            TokenResponseDto loginResponse = memberService.login(loginRequest);
            BaseResponseDto<TokenResponseDto> response = BaseResponseDto.success(
                    loginResponse, new BaseResponseDto.Meta("로그인 성공")
            );
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (AuthenticationException e) {
            BaseResponseDto<TokenResponseDto> response = BaseResponseDto.fail("로그인 실패");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            BaseResponseDto<TokenResponseDto> response = BaseResponseDto.fail(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
