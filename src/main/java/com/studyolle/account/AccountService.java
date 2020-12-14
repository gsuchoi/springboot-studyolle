package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void processNewAccount(SignUpForm signUpForm) {
        // 가입 확인 이메일 발송
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken(); // 토큰값 생성
        sendSignUpConfirmEmail(newAccount);
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        // 1. 회원가입 할 Account 생성.
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyCreatedByWeb(true)  /* 스터디는 웹과 관련된 것만 알림설정 */
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();
        // 2. repository를 이용해 account 저장.
        return accountRepository.save(account);
    }

    private void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디올래, 회원 가입 인증");    /*이메일의 제목*/
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());         /*이메일 본문으로 링크를 만들어서 전송*/
        javaMailSender.send(mailMessage);
    }

}
