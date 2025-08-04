package com.TingTing.util;

import java.security.SecureRandom;

public class CodeGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final String NUMERIC = "0123456789";

    // 예: 6자리 숫자 코드 생성
    public static String generateNumericCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(NUMERIC.charAt(random.nextInt(NUMERIC.length())));
        }
        return sb.toString();
    }

    // 필요시: 알파벳 포함한 코드
    public static String generateAlphaNumericCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
