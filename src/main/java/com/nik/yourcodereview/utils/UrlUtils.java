package com.nik.yourcodereview.utils;

public class UrlUtils {

    public static final String REDIRECT_L_PATH = "/l/";

    public static String addRedirectPrefix(String path) {
        return REDIRECT_L_PATH + path;
    }

}
