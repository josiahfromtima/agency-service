package com.tima.platform.service.social.validate;

import com.tima.platform.domain.SocialMedia;
import com.tima.platform.model.api.request.ClientSelectedSocialMedia;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/28/24
 */
public interface HandleValidatorService<T> {

    Mono<T> validateHandle(String handle, SocialMedia socialMedia, ClientSelectedSocialMedia media);
}
