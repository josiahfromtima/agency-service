package com.tima.platform.service.helper;

import com.google.gson.Gson;
import com.tima.platform.config.client.HttpConnectorService;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.response.FullUserProfileRecord;
import com.tima.platform.util.AppError;
import com.tima.platform.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.constant.AppConstant.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/21/23
 */
@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final HttpConnectorService connectorService;

    @Value("${user.profile.url}")
    private String userProfileUrl;

    public Mono<FullUserProfileRecord> getUserProfile(String token) {
        return connectorService.get(userProfileUrl, headers(token), String.class)
                .map(s -> gson(s, AppResponse.class))
                .flatMap(appResponse -> json(appResponse.getData()))
                .map(s -> gson(s, FullUserProfileRecord.class))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.message(t.getMessage())), BAD_REQUEST.value()));
    }
    private Map<String, String> headers(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put(ACCEPT, MEDIA_TYPE_JSON);
        headers.put(AUTHORIZATION, "Bearer " + token);
        return headers;
    }

    private Mono<String> json(Object data) {
        return Mono.just(AppUtil.gsonInstance().toJson(data) );
    }
    private  <T> T gson(String data, Class<T> returnType) {
        return AppUtil.gsonInstance().fromJson(data, returnType);
    }

}
