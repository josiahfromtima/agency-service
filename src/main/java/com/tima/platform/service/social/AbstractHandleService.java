package com.tima.platform.service.social;

import com.tima.platform.domain.SocialMedia;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.request.ClientSelectedSocialMedia;
import com.tima.platform.service.social.validate.HandleValidatorService;
import com.tima.platform.service.social.validate.InstagramValidatorService;
import com.tima.platform.util.AppError;
import com.tima.platform.util.LoggerHelper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/26/24
 */
@Service
@RequiredArgsConstructor
public class AbstractHandleService {

    private final LoggerHelper log = LoggerHelper.newInstance(AbstractHandleService.class.getName());
    private final InstagramValidatorService instagramValidatorService;

    private final Map<String, HandleValidatorService<ClientSelectedSocialMedia>> handler = new HashMap<>();

    @PostConstruct
    public void init() {
        handler.put("Instagram", instagramValidatorService);
    }


    public Mono<ClientSelectedSocialMedia> validateHandle(String type, String handle,
                                                          SocialMedia socialMedia, ClientSelectedSocialMedia media) {
        log.info(String.format("Validating the handle %s for %s ", handle, socialMedia.getName()) );
        return handler.get(type)
                .validateHandle(handle, socialMedia, media)
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.message(t.getMessage())), BAD_REQUEST.value()));
    }



}
