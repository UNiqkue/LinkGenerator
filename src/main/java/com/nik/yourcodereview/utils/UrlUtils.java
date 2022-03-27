package com.nik.yourcodereview.utils;

public class UrlUtils {

    public static final String L_PATH = "/l/";

    public static String addRedirectPrefix(String path) {
        return L_PATH + path;
    }

}
