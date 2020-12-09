package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    @InitBinder("signUpForm")
    // signUpForm 데이터를 받을 때 webDataBinder를 파라미터로 받아서 이 바인더에 validator를 추가함으로
    // valid 타입 "signUpForm"을 받을 때 303검사도 하고 signUpFormValidator 검사도 한다.
    // *signUpFormValidator.validate(signUpForm, errors);
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {   /*폼에 에러가 있으면 다시 폼으로 값을 리턴*/
            return "account/sign-up";
        }
        // 1. 회원가입 할 Account 생성.
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(signUpForm.getPassword()) // TODO encoding 해야함
                /*.emailVerified(false) 기본값들은 null이 들어가있으므로 생략가능*/
                .studyCreatedByWeb(true) /*스터디는 웹과 관련된 것만 알림설정*/
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();
        // 2. repository를 이용해 account 저장.
        Account newAccount = accountRepository.save(account);

        // 3. email 전송
        newAccount.generateEmailCheckToken(); // 토큰값 생성

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디올래, 회원 가입 인증");   /*이메일의 제목*/
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());  /*이메일 본문으로 링크를 만들어서 전송*/
        javaMailSender.send(mailMessage);

        return "redirect:/";
    }
}
