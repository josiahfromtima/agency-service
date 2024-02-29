package com.tima.platform.service.helper;

import com.tima.platform.converter.CampaignRegistrationConverter;
import com.tima.platform.converter.ClientSocialMediaConverter;
import com.tima.platform.domain.ClientSocialMedia;
import com.tima.platform.model.api.request.ClientSelectedSocialMedia;
import com.tima.platform.model.api.request.analytics.InfluencerAnalytics;
import com.tima.platform.model.api.request.analytics.PairMap;
import com.tima.platform.model.api.response.InfluencerApplicationRecord;
import com.tima.platform.model.api.response.instagram.business.BasicBusinessInsight;
import com.tima.platform.repository.CampaignRegistrationRepository;
import com.tima.platform.repository.ClientSocialMediaRepository;
import com.tima.platform.repository.SocialMediaRepository;
import com.tima.platform.service.social.insight.DefaultSocialBusinessInsight;
import com.tima.platform.service.social.insight.InsightService;
import com.tima.platform.service.social.insight.InstagramBusinessInsight;
import com.tima.platform.util.LoggerHelper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tima.platform.util.AppUtil.safeCastList;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/25/24
 */
@Service
@RequiredArgsConstructor
public class InfluencersInsightService {
    private final LoggerHelper log = LoggerHelper.newInstance(InfluencersInsightService.class.getName());
    private final CampaignRegistrationRepository registrationRepository;
    private final ClientSocialMediaRepository mediaRepository;
    private final SocialMediaRepository socialMediaRepository;
    private final InstagramBusinessInsight instagramBusinessInsight;
    private final DefaultSocialBusinessInsight socialBusinessInsight;
    Map<String, InsightService<?, ?, ?>> socialMediaInsights = new HashMap<>();

    @PostConstruct
    public void init() {
        socialMediaInsights.put("Instagram", instagramBusinessInsight);
    }
    private static final String BUSINESS_NAME = "BUSINESS_NAME";
    private static final String BUSINESS_OWNER = "BUSINESS_OWNER";
    private static final String BUSINESS_HANDLE = "BUSINESS_HANDLE";
    private static final String BUSINESS_IG_ID= "BUSINESS_IG_ID";
    private static final String BIOGRAPHY = "BIOGRAPHY";
    private static final String WEBSITE = "WEBSITE";
    private static final String PROFILE_PICTURE = "PROFILE_PICTURE";
    private static final String FOLLOWERS = "FOLLOWERS";
    private static final String TOTAL_MEDIA = "TOTAL_MEDIA";
    private static final String COMMENTS = "COMMENTS";
    private static final String LIKES = "LIKES";
    private static final String AVG_COMMENTS = "AVG_COMMENTS";
    private static final String AVG_LIKES = "AVG_LIKES";
    private static final String ENGAGEMENTS = "ENGAGEMENTS";


    public Mono<InfluencerAnalytics> getInfluencers(InfluencerApplicationRecord application) {
        log.info("Getting and building the Influencer for campaign ", application.campaignPublicId());
        return buildInfluencers(application.submittedBy(), application.campaignPublicId())
                .flatMap(metrics -> buildAnalytic(metrics, application));
    }

    private Mono<List<?>> buildInfluencers(String account, String publicId) {
        return getSocialMedia(publicId)
                        .flatMap(names ->  processInfluencerMetric(names, account).collectList());
    }
    
    private Flux<?> processInfluencerMetric(List<String> names, String account) {
        return Flux.fromIterable(names)
                .doOnNext(log::info)
                .flatMap(name -> socialMediaRepository.findByName(name)
                                .flatMap(socialMedia -> mediaRepository.findByUserId(account)
                                        .flatMap(clientSocialMedia ->
                                                socialMediaInsights.getOrDefault(name, socialBusinessInsight)
                                                .getUserBasicBusinessInsight(getSelectedMedia(name, clientSocialMedia),
                                                        socialMedia.getAccessToken() )
                                        )
                                )
                );
    }

    private Mono<InfluencerAnalytics> buildAnalytic(List<?> insights, InfluencerApplicationRecord applicationRecord) {
        PairMap pairMap = aggregate(insights);
        
        return Mono.just(InfluencerAnalytics.builder()
                        .applicationId(applicationRecord.applicationId())
                        .userName(applicationRecord.username())
                        .profilePicture(applicationRecord.profilePicture())
                        .socialMediaPlatforms(applicationRecord.socialMediaPlatform())
                        .applicationDate(applicationRecord.applicationDate())
                        .userPublicId(applicationRecord.submittedBy())
                        .insight(BasicBusinessInsight.builder()
                                .businessOwnerIgId(pairMap.alphaMap().getOrDefault(BUSINESS_OWNER, ""))
                                .businessIgId(pairMap.alphaMap().getOrDefault(BUSINESS_IG_ID, ""))
                                .businessName(pairMap.alphaMap().getOrDefault(BUSINESS_NAME, ""))
                                .businessHandle(pairMap.alphaMap().getOrDefault(BUSINESS_HANDLE, ""))
                                .biography(pairMap.alphaMap().getOrDefault(BIOGRAPHY, ""))
                                .profilePictureUrl(pairMap.alphaMap().getOrDefault(PROFILE_PICTURE, ""))
                                .website(pairMap.alphaMap().getOrDefault(WEBSITE, ""))
                                .followers(pairMap.numbericMap().getOrDefault(FOLLOWERS, 0L))
                                .totalMedia(pairMap.numbericMap().getOrDefault(TOTAL_MEDIA, 0L))
                                .totalComments(pairMap.numbericMap().getOrDefault(COMMENTS, 0L))
                                .avgComments(pairMap.numbericMap().getOrDefault(AVG_COMMENTS, 0L))
                                .totalLikes(pairMap.numbericMap().getOrDefault(LIKES, 0L))
                                .avgLikes(pairMap.numbericMap().getOrDefault(AVG_LIKES, 0L))
                                .avgEngagement(BigDecimal.valueOf(pairMap.numbericMap().getOrDefault(ENGAGEMENTS, 0L)))
                                .build())
                .build());
    }

    private PairMap aggregate(List<?> sights) {
        log.info("Building Aggregates ");
        Map<String, String> insightStringMap = new ConcurrentHashMap<>();
        Map<String, Long> insightNumbericMap = new ConcurrentHashMap<>();
        safeCastList(sights, BasicBusinessInsight.class).forEach(insight -> {
            insightStringMap.putIfAbsent(BUSINESS_IG_ID,  insight.businessIgId());
            insightStringMap.putIfAbsent(BUSINESS_OWNER, insight.businessOwnerIgId());
            insightStringMap.putIfAbsent(BUSINESS_NAME,  insight.businessName());
            insightStringMap.putIfAbsent(BUSINESS_HANDLE,  insight.businessHandle());
            insightStringMap.putIfAbsent(BIOGRAPHY,  insight.biography());
            insightStringMap.putIfAbsent(PROFILE_PICTURE,  insight.profilePictureUrl());
            insightStringMap.putIfAbsent(WEBSITE,  insight.website());
            insightNumbericMap.put(FOLLOWERS, insightNumbericMap.getOrDefault(FOLLOWERS, 0L) + insight.followers());
            insightNumbericMap.put(TOTAL_MEDIA, insightNumbericMap.getOrDefault(TOTAL_MEDIA, 0L) + insight.totalMedia());
            insightNumbericMap.put(COMMENTS, insightNumbericMap.getOrDefault(COMMENTS, 0L) + insight.totalComments());
            insightNumbericMap.put(LIKES, insightNumbericMap.getOrDefault(LIKES, 0L) + insight.totalLikes());
            insightNumbericMap.put(AVG_COMMENTS, insightNumbericMap.getOrDefault(AVG_COMMENTS, 0L) + insight.avgComments());
            insightNumbericMap.put(AVG_LIKES, insightNumbericMap.getOrDefault(AVG_LIKES, 0L) + insight.avgLikes());
            insightNumbericMap.put(ENGAGEMENTS, insightNumbericMap.getOrDefault(ENGAGEMENTS, 0L)
                    + insight.avgEngagement().longValue());
        });

        return PairMap.builder()
                .alphaMap(insightStringMap)
                .numbericMap(insightNumbericMap)
                .build();
    }

    private Mono<List<String>> getSocialMedia(String publicId) {
        return registrationRepository.findByPublicId(publicId)
                .map(CampaignRegistrationConverter::mapToRecord)
                .map(registration -> registration.overview().socialMediaPlatforms());
    }

    private ClientSelectedSocialMedia getSelectedMedia(String name, ClientSocialMedia media) {
        log.info("Getting selected media for analytics ", name);
        List<ClientSelectedSocialMedia> mediaList = ClientSocialMediaConverter.mapToRecord(media).selectedSocialMedia();
        return mediaList.stream().filter(media1 -> media1.name().equals(name))
                .findFirst()
                .orElse(ClientSelectedSocialMedia.builder().build());
    }

}
