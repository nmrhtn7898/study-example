package com.nuguri.example.validator;

import com.nuguri.example.model.AccountDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class AccountValidator {

    public boolean isPassEqualsWithRePass(String password, String rePassword, Errors errors) {
        boolean isEquals = password.equals(rePassword);
        if (!isEquals) {
            errors.rejectValue("password", "비밀번호와 비밀번호 재입력이 불일치 합니다.");
        }
        return isEquals;
    }

}
