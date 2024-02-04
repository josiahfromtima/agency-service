package com.tima.platform.service.social.insight;

import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.ClientSelectedSocialMedia;
import com.tima.platform.model.api.response.instagram.GraphApi;
import com.tima.platform.model.api.response.instagram.business.BasicBusinessInsight;
import com.tima.platform.model.api.response.instagram.insight.Demographic;
import com.tima.platform.model.constant.DemographicType;
import com.tima.platform.service.social.instagram.InstagramApiService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/27/24
 */
@Service
@RequiredArgsConstructor
public class InstagramBusinessInsight implements InsightService<BasicBusinessInsight, GraphApi<Demographic>> {
    private final LoggerHelper log = LoggerHelper.newInstance(InstagramBusinessInsight.class.getName());
    private final InstagramApiService apiService;

    private static final String INVALID_HANDLE = "Invalid instagram user handle";
    @Override
    public Mono<BasicBusinessInsight> getUserBasicBusinessInsight(ClientSelectedSocialMedia media, String token) {
        log.info("Get Instagram Basic Insight for ", media.handle());
        if(Objects.isNull(media.handle()))
            return handleOnErrorResume(new AppException(INVALID_HANDLE), BAD_REQUEST.value());
        return apiService.getBusinessDiscovery(token, media.handle());
    }

    @Override
    public Mono<GraphApi<Demographic>> getUserBasicBusinessInsight(ClientSelectedSocialMedia userId,
                                                      String token,
                                                      DemographicType type) {
        return apiService.getMetaBusinessInsight(userId.accessToken(), userId.businessId(), type);
    }
}
