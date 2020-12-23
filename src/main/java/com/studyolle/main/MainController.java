package com.studyolle.main;

import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    // TODO 1. 첫페이지로 가는 요청 핸들러 생성 : @CurrentUser로 account타입을 받을 예정. principal 정보를 가져오기위해서.
    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        //2. 핸들러 안에서 null 체크, 만약 인증한 사용자라면 model에 account 넣어주기(account가null)
        if  (account != null) {
            model.addAttribute(account);
        }
        return "index";
    }

}
