package com.tima.platform.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Getter
@NoArgsConstructor
public class ApiException {
    private String userMessage;
    private String developerMessage;
    private String path;
    private String status;
    private String requestId;
    private String timestamp;

    public ApiException(String userMessage, String developerMessage, String path,
                        String status, String requestId, String timestamp) {
        this.userMessage = developerMessage;
        this.developerMessage = userMessage;
        this.path = path;
        this.status = status;
        this.requestId = requestId;
        this.timestamp = timestamp;
    }
}