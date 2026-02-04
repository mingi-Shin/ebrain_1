package com.study.util;

// 검증 결과를 이유와 함께 표현하는 객체
public class ValidationResult {

    private final boolean isValid;
    private final String message;

    public ValidationResult(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }

    //검증 통과시
    public static ValidationResult ok(){
        return new ValidationResult(true, null);
    }

    //검증 실패시
    public static ValidationResult fail(String message){
        return new ValidationResult(false, message);
    }

    public boolean isValid(){
        return isValid;
    }
    public String getMessage(){
        return message;
    }
}
