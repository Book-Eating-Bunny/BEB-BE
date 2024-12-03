package com.beb.backend.controller;

import com.beb.backend.dto.AvailabilityResponseDto;
import com.beb.backend.dto.BaseResponseDto;
import com.beb.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class MemberController {

    private final MemberService memberService;

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
}
