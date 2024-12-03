package com.beb.backend.domain;

import jakarta.persistence.*;
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

    @Column(name = "email", nullable = false, unique = true)
    private String email;   // 로그인 ID로 사용

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false) // 성별 (M, F)
    private Gender gender;

    @Column(name = "profile_img_path")
    private String profileImgPath;

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
