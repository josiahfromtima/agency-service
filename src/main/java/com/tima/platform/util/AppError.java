package com.tima.platform.util;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/25/24
 */
public class AppError {
    private AppError() {}
    private static final String DUPLICATE = "Unique";
    private static final String DUPLICATE_MSG = "Record already exists";
    private static final String NOT_FOUND = "404 NOT_FOUND";
    private static final String BAD_REQUEST = "400 BAD_REQUEST";
    private static final String UNAUTHORIZED = "401 UNAUTHORIZED";
    private static final String FORBIDDEN = "403 FORBIDDEN";
    private static final String DEFAULT_MSG = "Could not process request at this time. Please contact the admin";

    public static String message(String error) {
        if(error.toLowerCase().contains(DUPLICATE.toLowerCase())) return DUPLICATE_MSG;
        else if(error.contains(NOT_FOUND)) return error.split(NOT_FOUND)[1].replace("\"", "");
        else if(error.contains(BAD_REQUEST)) return error.split(BAD_REQUEST)[1].replace("\"", "");
        else if(error.contains(UNAUTHORIZED)) return error.split(UNAUTHORIZED)[1].replace("\"", "");
        else if(error.contains(FORBIDDEN)) return error.split(FORBIDDEN)[1].replace("\"", "");
        else return DEFAULT_MSG;
    }
}
