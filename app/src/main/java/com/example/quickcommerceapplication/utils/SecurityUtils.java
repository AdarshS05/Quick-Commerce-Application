package com.example.quickcommerceapplication.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {
    private static final String STATIC_SALT = "QuickCommerce_Salt_2026";

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((password + STATIC_SALT).getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }
}
