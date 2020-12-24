package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id") //id만 사용하는 이유는 모르겠음
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {
    // * annotation @Entity를 사용할때는 pk가 있어야 함.
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified; // 이메일 인증을 위한 유무.

    private String emailCheckToken; // 이메일 검증을 위한 토큰값.

    private LocalDateTime emailCheckTokenGeneratedAt; //이메일검증 토큰값 생성시간.

    private LocalDateTime joinedAt; // 인증된 회원의 가입날짜.

    //기본 프로필
    private String bio;

    private String url;

    private String occupation; // 직업

    private String location;  // varchar(255)

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    // 알림 설정 관련
    private boolean studyCreatedByEmail;  // 스터디가 만들어졌다는 걸 받을 방법.

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;  // 스터디가 운영하는 모임에 가입신청 결과를 받는 방법.

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;  // 스터디에 대한 변경 사항을 받는 방법.

    private boolean studyUpdatedByWeb;

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString(); // random한 uuid를 생성.
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }
    /*메일 유무 확인, 가입 날짜*/
    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }
}
