package com.beb.backend.domain;

import com.beb.backend.common.ValidationRegexConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "member") // 테이블명 user로 사용 시 충돌나는 DB가 있어 member로 정하고 엔터티도 Member로 함.
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {   // 회원
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @Column(name = "member_id")
    private Long id;

    @NotNull
    @Pattern(regexp = ValidationRegexConstants.EMAIL_REGEX,
            message = "잘못된 이메일 형식")
    @Column(name = "email", nullable = false, unique = true)
    private String email;   // 로그인 ID로 사용

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Pattern(regexp = ValidationRegexConstants.NICKNAME_REGEX,
            message = "닉네임은 8자 이하의 한글, 영어, 숫자만 허용됩니다.")
    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @NotNull
    @Min(value = 1) @Max(value = 100)
    @Column(name = "age", nullable = false)
    private Integer age;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false) // 성별 (M, F)
    private Gender gender;

    @Column(name = "profile_img_path")
    private String profileImgPath;

    @NotNull
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    public enum Gender {
        M, F
    }

    @Builder
    public Member(String email, String password, String nickname, Integer age, Gender gender, String profileImgPath) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.profileImgPath = profileImgPath;
    }
}
