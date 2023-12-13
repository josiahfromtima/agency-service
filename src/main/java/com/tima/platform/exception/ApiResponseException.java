package com.tima.platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
public class ApiResponseException extends ResponseStatusException {
    public ApiResponseException(HttpStatus status, String message, Throwable e) {
        super(status, message, e);
    }

}
