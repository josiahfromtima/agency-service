package com.tima.platform.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class ApiGlobalErrorAttributes extends DefaultErrorAttributes {
    private HttpStatus status = HttpStatus.BAD_REQUEST;
    private String message = "default message";

    @Data
    public static class AtomicApiError {
        @JsonProperty("developer_message")
        @SerializedName("developer_message")
        private String message;
    }
}
