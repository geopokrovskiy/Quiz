package com.geopokrovskiy.util;

public class XorEncrypting {
    private static final char key = 'F';

    public static String encryptString(String string) {
        String result = "";
        for (char c : string.toCharArray()) {
            int encryptedInt = c ^ key;
            char encryptedChar = (char) encryptedInt;
            result += encryptedChar;
        }
        return result;
    }
}