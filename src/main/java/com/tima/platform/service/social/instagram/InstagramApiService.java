package com.tima.platform.service.social.instagram;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tima.platform.config.client.HttpConnectorService;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.response.instagram.GraphApi;
import com.tima.platform.model.api.response.instagram.account.IGUser;
import com.tima.platform.model.api.response.instagram.account.MeAccount;
import com.tima.platform.model.api.response.instagram.business.BasicBusinessInsight;
import com.tima.platform.model.api.response.instagram.business.BusinessSummary;
import com.tima.platform.model.api.response.instagram.business.MediaItem;
import com.tima.platform.model.api.response.instagram.insight.Breakdown;
import com.tima.platform.model.api.response.instagram.insight.Demographic;
import com.tima.platform.model.api.response.instagram.insight.FollowerDemographic;
import com.tima.platform.model.api.response.instagram.insight.result.Breakdowns;
import com.tima.platform.model.api.response.instagram.insight.result.Results;
import com.tima.platform.model.api.response.instagram.token.LongLivedAccessToken;
import com.tima.platform.model.constant.DemographicType;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
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
    @Value("${social.ig.demographicsCountry}")
    private String countryDemo;
    @Value("${social.ig.demographicsCity}")
    private String cityDemo;
    @Value("${social.ig.demographicsPerson}")
    private String personDemo;


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

    public Mono<FollowerDemographic> getMetaBusinessInsight(String token, String instagramId, DemographicType type) {
        log.info("Getting the Meta Business Insight with Instagram Account ", instagramId, " for ", type);
        if(type.equals(DemographicType.COUNTRY)) return getBusinessInsightForCountry(token, instagramId);
        else if(type.equals(DemographicType.CITY)) return getBusinessInsightForCity(token, instagramId);
        else return getBusinessInsightForBio(token, instagramId);
    }

    public Mono<FollowerDemographic> getBusinessInsightForCountry(String token, String instagramId) {
        log.info("Getting the Business Insight for country with Instagram Account ", instagramId);
        return connectorService.get(template(countryDemo, instagramId), headers(token), GraphApi.class)
                .doOnNext(log::info)
                .flatMap(this::toDemographic);
    }

    public Mono<FollowerDemographic> getBusinessInsightForCity(String token, String instagramId) {
        log.info("Getting the Business Insight for city with Instagram Account ", instagramId);
        return connectorService.get(template(cityDemo, instagramId), headers(token), GraphApi.class)
                .doOnNext(log::info)
                .flatMap(this::toDemographic);
    }

    public Mono<FollowerDemographic> getBusinessInsightForBio(String token, String instagramId) {
        log.info("Getting the Business Insight for age and gender with Instagram Account ", instagramId);
        return connectorService.get(template(personDemo, instagramId), headers(token), GraphApi.class)
                .doOnNext(log::info)
                .flatMap(this::toDemographic);
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
                        .avgEngagement(calculateEngagementRate(
                                counts.get(COMMENTS)+counts.get(LIKES),
                                businessSummary.businessDiscovery().followersCount()) )
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

    private BigDecimal calculateEngagementRate(long totalEngagements, long totalFollowers) {
        log.info("Engagements ", totalEngagements, " Followers ", totalFollowers);
        if(totalFollowers == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf((double) totalEngagements / totalFollowers)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.UP);
    }

    private Mono<FollowerDemographic> toDemographic(GraphApi<Demographic> graphApi) {
        if(graphApi.data().isEmpty()) return Mono.empty();

        JsonObject jsonObject = getJsonObject(json(graphApi.data()));
        Demographic demographic = (Objects.isNull(jsonObject)) ? defaultDemo() : buildDemoGraphic(jsonObject);

        return Mono.just(FollowerDemographic.builder()
                        .data(List.of(demographic))
                .build());
    }

    private Demographic defaultDemo() {
        return Demographic.builder()
                .totalValue(Breakdown.builder().breakdowns(new ArrayList<>()).build())
                .build();
    }

    private Demographic buildDemoGraphic(JsonObject jsonObject) {
        Set<String> keys = jsonObject.keySet();
        log.info("Demographic ", keys);
        return Demographic.builder()
                .name(getStringValue(jsonObject,"name"))
                .title(getStringValue(jsonObject,"title"))
                .description(getStringValue(jsonObject,"description"))
                .period(getStringValue(jsonObject,"period"))
                .totalValue(buildBreakdown(jsonObject.getAsJsonObject("total_value")))
                .build();
    }

    private Breakdown buildBreakdown(JsonObject jsonObject) {
        JsonArray jsonArray = jsonObject.getAsJsonArray("breakdowns")
                .get(0)
                .getAsJsonObject()
                .getAsJsonArray("results");

        List<Breakdowns> breakdownsList = jsonArray.asList()
                .stream()
                .map(jsonElement -> Breakdowns.builder().results(
                        Results.builder()
                                .dimensionValues(getList( jsonElement
                                        .getAsJsonObject()
                                        .getAsJsonArray("dimension_values")
                                        .asList()) )
                                .value(jsonElement.getAsJsonObject().get("value").getAsLong())
                                .build()
                        )
                        .build() )
                        .toList();
        return Breakdown.builder()
                .breakdowns(breakdownsList)
                .build();
    }

    private String getStringValue(JsonObject jsonObject, String key) {
        return jsonObject.get(key).getAsString();
    }

    private JsonObject getJsonObject(String graphApi) {
        JsonArray jsonArray = JsonParser
                .parseString(graphApi)
                .getAsJsonArray();
        if(jsonArray.isEmpty()) return null;
        return jsonArray.get(0).getAsJsonObject();
    }

    private List<String> getList(List<JsonElement> jsonElements) {
        return jsonElements.stream()
                .map(JsonElement::getAsString)
                .toList();
    }


    private static String json(Object value) {
        return AppUtil.gsonInstance().toJson(value);
    }

    private Map<String, String> headers(String accessToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put(ACCEPT, MEDIA_TYPE_JSON);
        headers.put(CONTENT_TYPE, MEDIA_TYPE_JSON);
        headers.put(AUTHORIZATION, "Bearer " + accessToken);
        return headers;
    }

}
