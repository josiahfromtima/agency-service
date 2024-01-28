package com.tima.platform.service.social.instagram;

import com.tima.platform.config.client.HttpConnectorService;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.response.instagram.GraphApi;
import com.tima.platform.model.api.response.instagram.account.IGUser;
import com.tima.platform.model.api.response.instagram.account.MeAccount;
import com.tima.platform.model.api.response.instagram.business.BasicBusinessInsight;
import com.tima.platform.model.api.response.instagram.business.BusinessSummary;
import com.tima.platform.model.api.response.instagram.business.MediaItem;
import com.tima.platform.model.api.response.instagram.token.LongLivedAccessToken;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tima.platform.model.constant.AppConstant.*;
import static com.tima.platform.util.AppUtil.buildAppResponse;
import static com.tima.platform.util.AppUtil.gsonInstance;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/18/24
 */
@Service
@RequiredArgsConstructor
public class InstagramApiService {
    private final LoggerHelper log = LoggerHelper.newInstance(InstagramApiService.class.getName());
    private final HttpConnectorService connectorService;

    @Value("${social.ig.account}")
    private String accountEndpoint;
    @Value("${social.ig.userId}")
    private String igUserEndpoint;
    @Value("${social.ig.longLivedToken}")
    private String longLiveTokenEndpoint;
    @Value("${social.ig.businessDiscovery}")
    private String businessDiscoveryEndpoint;
    @Value("${social.ig.businessAccount}")
    private String businessAccount;


    private static final String COMMENTS = "comments";
    private static final String LIKES = "likes";

    public Mono<AppResponse> getAccount(String token) {
        log.info("Getting the Instagram Page ");
        return connectorService.get(accountEndpoint, headers(token), GraphApi.class)
                .doOnNext(log::info)
                .flatMap(graphApi -> getBusinessAccount(token, convert(graphApi).id() ));
    }
    public Mono<AppResponse> getBusinessAccount(String token, String pageId) {
        log.info("Getting the Instagram Business Account ");
        return connectorService.get(template(igUserEndpoint, pageId), headers(token), IGUser.class)
                .doOnNext(log::info)
                .map(graphApi -> buildAppResponse(graphApi, "Graph Api"));
    }
    public Mono<BasicBusinessInsight> getBusinessDiscovery(String token, String handle) {
        log.info("Getting the Instagram Business Info ");
        return connectorService.get(template(businessDiscoveryEndpoint, businessAccount, handle),
                        headers(token), BusinessSummary.class)
                .doOnNext(log::info)
                .flatMap(this::calculateMedia);
    }
    public Mono<LongLivedAccessToken> getLTTLAccessToken(String token) {
        log.info("Getting the Long Lived Access Token");
        return connectorService.get(template(longLiveTokenEndpoint, token),
                        headers(token), LongLivedAccessToken.class)
                .doOnNext(log::info);
    }

    private String template(String template, Object... params) {
        return String.format(template, params);
    }

    private Mono<BasicBusinessInsight> calculateMedia(BusinessSummary businessSummary) {
        Map<String, Long> counts = new ConcurrentHashMap<>();
        counts.put(COMMENTS, 0L);
        counts.put(LIKES, 0L);
        businessSummary
                .businessDiscovery()
                .media()
                .data()
                .forEach(mediaItem -> reduce(mediaItem, counts));
        return Mono.just(BasicBusinessInsight.builder()
                        .businessOwnerIgId(businessSummary.id())
                        .businessIgId(businessSummary.businessDiscovery().id())
                        .businessHandle(businessSummary.businessDiscovery().username())
                        .businessName(businessSummary.businessDiscovery().name())
                        .biography(businessSummary.businessDiscovery().biography())
                        .website(businessSummary.businessDiscovery().website())
                        .followers(businessSummary.businessDiscovery().followersCount())
                        .totalMedia(businessSummary.businessDiscovery().mediaCount())
                        .totalComments(counts.get(COMMENTS))
                        .totalLikes(counts.get(LIKES))
                .build() );
    }

    private void reduce(MediaItem mediaItem, Map<String, Long> accumulator) {
        accumulator.put(COMMENTS, accumulator.get(COMMENTS) + mediaItem.commentsCount());
        accumulator.put(LIKES, accumulator.get(LIKES) + mediaItem.likeCount());
    }

    private MeAccount convert(GraphApi<MeAccount> graphApi) {
        log.info("Ok see ", graphApi.data());
        return gsonInstance().fromJson(gsonInstance().toJson(graphApi.data().get(0)), MeAccount.class);
    }

    private Map<String, String> headers(String accessToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put(ACCEPT, MEDIA_TYPE_JSON);
        headers.put(CONTENT_TYPE, MEDIA_TYPE_JSON);
        headers.put(AUTHORIZATION, "Bearer " + accessToken);
        return headers;
    }

}
