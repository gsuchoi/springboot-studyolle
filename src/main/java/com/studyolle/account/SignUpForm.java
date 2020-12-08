package com.studyolle.account;

import lombok.Data;
import org.springframework.ui.Model;

@Data
public class SignUpForm {

    private String nickname;

    private String email;

    private String password;
}
