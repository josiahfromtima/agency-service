package com.tima.platform.service.social.insight;

import com.tima.platform.converter.ClientSocialMediaConverter;
import com.tima.platform.domain.ClientSocialMedia;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.ClientSelectedSocialMedia;
import com.tima.platform.model.constant.DemographicType;
import com.tima.platform.repository.ClientSocialMediaRepository;
import com.tima.platform.service.social.SocialMediaService;
import com.tima.platform.util.LoggerHelper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND_INFLUENCER;
import static com.tima.platform.util.AppUtil.buildAppResponse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/27/24
 */
@Service
@RequiredArgsConstructor
public class ClientBusinessInsightService {
    private final LoggerHelper log = LoggerHelper.newInstance(ClientBusinessInsightService.class.getName());
    private final ClientSocialMediaRepository mediaRepository;
    private final SocialMediaService socialMediaService;
    private final InstagramBusinessInsight instagramBusinessInsight;
    Map<String, InsightService<?, ?, ?>> socialMediaInsights = new HashMap<>();

    private static final String INVALID_NAME = "Selected Social media feature is not available yet. Or name is invalid";
    private static final String INVALID_TYPE = "Selected demographic type is invalid";
    private static final String INSIGHT_MSG = "Insight of %s Executed successfully";
    private static final String ERROR_MSG = "Insight could not be retrieved Please contact support";

    @PostConstruct
    public void init() {
        socialMediaInsights.put("Instagram", instagramBusinessInsight);
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getDemographicTypes() {
        return Mono.just(buildAppResponse(DemographicType.values(), INSIGHT_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getBasicInsights(String publicId, String name) {
        log.info("Get Basic Insight from ", name);
        if(Objects.isNull(socialMediaInsights.get(name)))
            return handleOnErrorResume(new AppException(INVALID_NAME), BAD_REQUEST.value());
        return socialMediaService.getSocialMedia(name)
                .flatMap(socialMedia -> mediaRepository.findByUserId(publicId)
                        .flatMap(clientSocialMedia -> socialMediaInsights.get(name)
                                .getUserBasicBusinessInsight(getSelectedMedia(name, clientSocialMedia),
                                        socialMedia.getAccessToken())
                        )
                ).map(businessInsight -> buildAppResponse(businessInsight, String.format(INSIGHT_MSG, name)))
                .switchIfEmpty(handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getBusinessInsights(String name, String publicId, String demoType) {
        log.info("Get Business Client Insight from ", name, " ", publicId);
        if(Objects.isNull(socialMediaInsights.get(name)))
            return handleOnErrorResume(new AppException(INVALID_NAME), BAD_REQUEST.value());
        DemographicType type = parseType(demoType);
        if(Objects.isNull(type)) return handleOnErrorResume(new AppException(INVALID_TYPE), BAD_REQUEST.value());
        return socialMediaService.getSocialMedia(name)
                .flatMap(socialMedia ->  mediaRepository.findByUserId(publicId)
                        .flatMap(clientSocialMedia -> socialMediaInsights.get(name)
                                .getUserBasicBusinessInsight(getSelectedMedia(name, clientSocialMedia),
                                        socialMedia.getAccessToken(), type) )
                ).map(businessInsight -> buildAppResponse(businessInsight, String.format(INSIGHT_MSG, name)))
                .switchIfEmpty(handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()));
    }
    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getBusinessBasicInsights(String name, String handle) {
            log.info("Get Other Business Basic Insight from ", name, " ", handle);
            if(Objects.isNull(socialMediaInsights.get(name)))
                return handleOnErrorResume(new AppException(INVALID_NAME), BAD_REQUEST.value());
            return socialMediaService.getSocialMedia(name)
                    .flatMap(socialMedia ->  socialMediaInsights.get(name)
                                    .getUserBasicBusinessInsight(build(handle), socialMedia.getAccessToken())
                    ).map(businessInsight -> buildAppResponse(businessInsight, String.format(INSIGHT_MSG, name)))
                    .switchIfEmpty(handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getBusinessInsightMetrics(String name, String publicId) {
        log.info("Get Other Business Insight Metrics from ", name, " ", publicId);
        if(Objects.isNull(socialMediaInsights.get(name)))
            return handleOnErrorResume(new AppException(INVALID_NAME), BAD_REQUEST.value());
        return socialMediaService.getSocialMedia(name)
                .flatMap(socialMedia ->  mediaRepository.findByUserId(publicId)
                        .flatMap(clientSocialMedia -> socialMediaInsights.get(name)
                                .getUserBusinessInsightMetrics(getSelectedMedia(name, clientSocialMedia),
                                        socialMedia.getAccessToken()) )
                ).map(businessInsight -> buildAppResponse(businessInsight, String.format(INSIGHT_MSG, name)))
                .switchIfEmpty(handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()));
    }

    private ClientSelectedSocialMedia getSelectedMedia(String name, ClientSocialMedia media) {
        log.info("Getting selected media");
        List<ClientSelectedSocialMedia> mediaList = ClientSocialMediaConverter.mapToRecord(media).selectedSocialMedia();
        return mediaList.stream().filter(media1 -> media1.name().equals(name))
                .findFirst()
                .orElse(ClientSelectedSocialMedia.builder().build());
    }

    private ClientSelectedSocialMedia build(String handle) {
        return ClientSelectedSocialMedia.builder().handle(handle).build();
    }

    private DemographicType parseType(String type) {
        try {
            return DemographicType.valueOf(type);
        }catch (Exception e) {
            return null;
        }
    }

}
