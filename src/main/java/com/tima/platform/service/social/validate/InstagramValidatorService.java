package com.tima.platform.service.social.validate;

import com.tima.platform.domain.SocialMedia;
import com.tima.platform.model.api.request.ClientSelectedSocialMedia;
import com.tima.platform.model.api.response.instagram.business.BasicBusinessInsight;
import com.tima.platform.service.social.instagram.InstagramApiService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/28/24
 */
@Service
@RequiredArgsConstructor
public class InstagramValidatorService implements HandleValidatorService<ClientSelectedSocialMedia> {
    private final LoggerHelper log = LoggerHelper.newInstance(InstagramValidatorService.class.getName());
    private final InstagramApiService instagramApiService;
    @Override
    public Mono<ClientSelectedSocialMedia> validateHandle(String handle,
                                                          SocialMedia socialMedia,
                                                          ClientSelectedSocialMedia media) {
        return runValidation(handle, socialMedia)
                .flatMap(insight -> reBuild(media, insight, socialMedia));
    }

    /**
     * Handle validation for Instagram
     * @param handle - The user handle of the social media
     * @param socialMedia - the social media itself
     * @return Mono<BasicBusinessInsight>
     */
    private Mono<BasicBusinessInsight> runValidation(String handle, SocialMedia socialMedia){
            return validateHandle(handle, socialMedia.getAccessToken());
    }
    private Mono<BasicBusinessInsight> validateHandle(String handle, String token) {
        return instagramApiService.getBusinessDiscovery(token, handle);
    }

    private Mono<ClientSelectedSocialMedia> reBuild(ClientSelectedSocialMedia media,
                                                    BasicBusinessInsight insight,
                                                    SocialMedia socialMedia) {
        return Mono.just(ClientSelectedSocialMedia.builder()
                .name(media.name())
                .businessId(insight.businessIgId())
                .handle(media.handle())
                .accessToken(media.accessToken())
                .expiresIn(media.expiresIn())
                .logo(socialMedia.getLogo())
                .build());
    }
}
