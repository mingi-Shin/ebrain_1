package com.study.util;

import java.util.regex.Pattern;

//비밀번호 검증 클래스
public class PasswordValidator {

    // 패턴 규칙 명시
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*()-+=_]).{4,15}$";

    //문자열 패턴화
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean isValid(String password){
        if(password == null){
            return false;
        }
        return pattern.matcher(password).matches();
    }
}
