package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
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
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
    // signUpForm 데이터를 받을 때 webDataBinder를 파라미터로 받아서 이 바인더에 validator를 추가함으로
    // valid 타입 "signUpForm"을 받을 때 303검사도 하고 signUpFormValidator 검사도 한다.
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
        if (errors.hasErrors()) {   /*폼에 에러가 있으면 다시 폼으로 리턴*/
            return "account/sign-up";
        }

        Account account = accountService.processNewAccount(signUpForm);
        // TODO 1.서비스에서 로그인 2. 여기서 로그인
        accountService.login(account);
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";
        if ( account == null ) {
            model.addAttribute("error", "wrong.email");
            return view;
        }
        //account에 있는 토큰이랑 내가 받아온 토큰이랑 같은지 비교.
        if (!account.isValidToken(token)){
            model.addAttribute("error", "wrong.token");
            return view;
        }
        /*이메일이 토큰과 일치하는지*/
        account.completeSignUp();
        accountService.login(account);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return view;
    }

}
