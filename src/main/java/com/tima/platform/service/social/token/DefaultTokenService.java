package com.tima.platform.service.social.token;

import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.response.instagram.token.LongLivedAccessToken;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/28/24
 */
@Service
@RequiredArgsConstructor
public class DefaultTokenService implements TokenService<LongLivedAccessToken> {
    private final LoggerHelper log = LoggerHelper.newInstance(DefaultTokenService.class.getName());
    private static final String INVALID_MEDIA = "Social Media Requested is currently not available";
    @Override
    public Mono<LongLivedAccessToken> getLongLivedToken(String token) {
        log.error(INVALID_MEDIA);
        return handleOnErrorResume(new AppException(INVALID_MEDIA), BAD_REQUEST.value());
    }
}
