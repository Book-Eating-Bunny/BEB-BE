package com.beb.backend.common;

public class ValidationRegexConstants {
    /*
     * https://owasp.org/www-community/OWASP_Validation_Regex_Repository
     * OWASP에서 유효성 검사를 위해 제공하는 정규식 라이브러리에서 email 정규식 내용을 가져옴
     */
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
    public static final String PASSWORD_REGEX = "^[a-zA-Z0-9!@#$%^&*]{8,20}$";
    public static final String NICKNAME_REGEX = "^[가-힣a-zA-Z0-9]{1,8}$";
    public static final String ISBN_REGEX = "^[0-9]{13}$";
}
