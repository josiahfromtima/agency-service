package com.tima.platform.service.social;

import com.tima.platform.converter.SocialMediaConverter;
import com.tima.platform.domain.SocialMedia;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.response.SocialMediaRecord;
import com.tima.platform.repository.SocialMediaRepository;
import com.tima.platform.service.IndustryService;
import com.tima.platform.service.social.instagram.InstagramApiService;
import com.tima.platform.util.AppError;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.ADMIN;
import static com.tima.platform.util.AppUtil.buildAppResponse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/25/24
 */
@Service
@RequiredArgsConstructor
public class SocialMediaService {
    private final LoggerHelper log = LoggerHelper.newInstance(IndustryService.class.getName());
    private final SocialMediaRepository mediaRepository;
    private final InstagramApiService instagramApiService;

    @Value("${social.expires}")
    private long timeToLive;
    private static final long MINUS_DAYS = 5;

    private static final String SOCIAL_MSG = "Social Media request executed successfully";
    private static final String INVALID_SOCIAL= "The social media name is invalid";

    public Mono<AppResponse> getSocialMedias() {
        log.info("Getting all the available social media Records...");
        return mediaRepository.findAll()
                .collectList()
                .map(SocialMediaConverter::mapToRecords)
                .map(r -> buildAppResponse(r, SOCIAL_MSG));
    }
    @PreAuthorize(ADMIN)
    public Mono<AppResponse> getAllSocialMedia() {
        log.info("Getting all the available social media Records by Admin...");
        return mediaRepository.findAll()
                .collectList()
                .map(SocialMediaConverter::mapToFullRecords)
                .map(r -> buildAppResponse(r, SOCIAL_MSG));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> addSocialMedia(SocialMediaRecord socialMediaRecord) {
        log.info("Adding new Social Media Record...");
        return instagramApiService.getLTTLAccessToken(socialMediaRecord.accessToken())
                .flatMap(longLivedAccessToken -> {
                    SocialMedia socialMedia = SocialMediaConverter.mapToEntity(socialMediaRecord);
                    socialMedia.setAccessToken(longLivedAccessToken.accessToken());
                    socialMedia.setExpiresIn((int) timeToLive);
                    socialMedia.setExpiresOn(Instant.now().plus(timeToLive - MINUS_DAYS, ChronoUnit.DAYS));
                    return mediaRepository.save(socialMedia);
                })
                .map(SocialMediaConverter::mapToFullRecord)
                .map(r -> buildAppResponse(r, SOCIAL_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.message(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> editSocialMedia(SocialMediaRecord socialMediaRecord) {
        log.info("Editing Social Media Record... ", socialMediaRecord.name());
        return getSocialMedia(socialMediaRecord.name())
                .flatMap(socialMedia -> instagramApiService.getLTTLAccessToken(socialMediaRecord.accessToken())
                    .flatMap(longLivedAccessToken -> {
                        socialMedia.setAccessToken(longLivedAccessToken.accessToken());
                        socialMedia.setLogo(socialMediaRecord.logo());
                        socialMedia.setExpiresOn(Instant.now().plus(timeToLive - MINUS_DAYS, ChronoUnit.DAYS));
                        return mediaRepository.save(socialMedia);
                    })
                ).map(SocialMediaConverter::mapToFullRecord)
                .map(r -> buildAppResponse(r, SOCIAL_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.message(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> deleteSocialMedia(String name) {
        log.info("Delete the Social Media Record  ", name);
        return getSocialMedia(name)
                .flatMap(mediaRepository::delete)
                .then(Mono.fromCallable(() -> buildAppResponse(name + " Deleted", SOCIAL_MSG)));
    }

    private Mono<SocialMedia> getSocialMedia(String name) {
        return mediaRepository.findByName(name)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_SOCIAL), BAD_REQUEST.value()));
    }
}
