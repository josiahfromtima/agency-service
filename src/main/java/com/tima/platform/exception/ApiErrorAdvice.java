package com.tima.platform.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Slf4j
@Component
@Order(-2)
public class ApiErrorAdvice extends AbstractErrorWebExceptionHandler {
    private final MessageSource messageSource;

    public ApiErrorAdvice(ErrorAttributes g,
                          ApplicationContext applicationContext,
                          ServerCodecConfigurer serverCodecConfigurer,
                          MessageSource messageSource) {
        super(g, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
        this.messageSource = messageSource;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        ErrorAttributeOptions options = ErrorAttributeOptions.defaults().including(ErrorAttributeOptions.Include.MESSAGE);
        Map<String, Object> errorPropertiesMap = getErrorAttributes(request, options);
        log.error("{}", errorPropertiesMap.get("message"));
        return buildResponse(errorPropertiesMap);
    }

    private Mono<ServerResponse> buildResponse(Map<String, Object> errorAttr) {
        return switch (Integer.parseInt(getAsString("status", errorAttr, "500"))) {
            case 400 -> ServerResponse.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(getException(message("error.code.400"), errorAttr));
            case 401 -> ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(getException(message("error.code.401"), errorAttr));
            case 403 -> ServerResponse.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(getException(message("error.code.403"), errorAttr));
            case 404 -> ServerResponse.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(getException(message("error.code.404"), errorAttr));
            default -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(getException(message("error.code.500"), errorAttr));
        };
    }

    private String message(String s) {
        return messageSource.getMessage( s, null, Locale.ENGLISH);
    }

    private String getAsString(String key, Map<String, Object> errors, String defaultValue) {
        return Objects.isNull(errors.get(key)) ? "Requested URL is invalid"
                : String.valueOf(errors.getOrDefault(key, defaultValue));
    }

    private ApiException getException(String code, Map<String, Object> errors) {
        return new ApiException(code,
                getAsString("message", errors, ""),
                getAsString("path", errors, ""),
                getAsString("error", errors, "Unknown Error"),
                getAsString("requestId", errors, ""),
                getAsString("timestamp", errors, ""));
    }
}
