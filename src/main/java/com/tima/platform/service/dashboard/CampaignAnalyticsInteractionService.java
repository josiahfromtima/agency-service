package com.tima.platform.service.dashboard;

import com.tima.platform.model.api.request.analytics.InteractionSummary;
import com.tima.platform.model.api.response.instagram.insight.metrics.InsightMetrics;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tima.platform.util.AppUtil.safeCastList;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/22/24
 */
@Service
@RequiredArgsConstructor
public class CampaignAnalyticsInteractionService {
    private final LoggerHelper log = LoggerHelper.newInstance(CampaignAnalyticsInteractionService.class.getName());
    private final CampaignAnalyticsKpiService kpiService;
    private final ClientSocialMediaRepository mediaRepository;
    private final SocialMediaRepository socialMediaRepository;
    private final InstagramBusinessInsight instagramBusinessInsight;
    private final DefaultSocialBusinessInsight socialBusinessInsight;
    Map<String, InsightService<?, ?, ?>> socialMediaInsights = new HashMap<>();

    @PostConstruct
    public void init() {
        socialMediaInsights.put("Instagram", instagramBusinessInsight);
    }
    private static final String ENGAGEMENT = "accounts_engaged";
    private static final String REACH = "reach";
    private static final String IMPRESSIONS = "impressions";
    private static final String LIKES = "likes";
    private static final String COMMENTS = "comments";
    private static final String SHARED = "shares";

    public Mono<InteractionSummary> getSummary(String publicId) {
        log.info("Getting and building the interaction summary for campaign influencers");
        return kpiService.getInfluencers(publicId)
                .flatMap(accounts -> buildSummary(accounts, publicId))
                .flatMap(this::calculateSummaries)
                .map(this::summarize);
    }

    private Mono<List<?>> buildSummary(List<String> accounts, String publicId) {
        return kpiService.getSocialMedia(publicId)
                .flatMap(names ->  processInteractions(names, accounts).collectList().doOnNext(log::info) );
    }

    private Flux<?> processInteractions(List<String> names, List<String> accounts) {
        return Flux.fromIterable(names)
                .flatMap(name ->  Flux.fromIterable(accounts)
                        .flatMap(account -> socialMediaRepository.findByName(name)
                                .flatMap(socialMedia -> mediaRepository.findByUserId(account)
                                        .flatMap(clientSocialMedia ->
                                                socialMediaInsights.getOrDefault(name, socialBusinessInsight)
                                                .getUserBusinessInsightMetrics(
                                                        kpiService.getSelectedMedia(name, clientSocialMedia),
                                                        kpiService.getSelectedMedia(name, clientSocialMedia)
                                                                .accessToken() )
                                        )
                                )
                        )
                );
    }

    private Mono<List<InteractionSummary>> calculateSummaries(List<?> insightMetrics) {
        InteractionSummary summary = InteractionSummary.builder().build();
        log.info(" summaries ", insightMetrics.get(0));
        return Mono.just(
                insightMetrics.stream()
                .map(metrics -> calculateMetrics((List<?>) metrics, summary))
                .toList()
        ).doOnNext(log::info);
    }

    private InteractionSummary calculateMetrics(List<?> metrics, InteractionSummary summary) {
        Map<String, Long> metricMap = newInsightMap(summary);
        safeCastList(metrics, InsightMetrics.class).forEach(insightMetric->
            metricMap.put(insightMetric.name(),
                    metricMap.getOrDefault(insightMetric.name(), 0L) + insightMetric.totalValue().value())
        );
        return InteractionSummary.builder()
                .engagement(metricMap.get(ENGAGEMENT))
                .impressions(metricMap.get(IMPRESSIONS))
                .reach(metricMap.get(REACH))
                .comments(metricMap.get(COMMENTS))
                .likes(metricMap.get(LIKES))
                .shared(metricMap.get(SHARED))
                .build();
    }

    private InteractionSummary summarize(List<InteractionSummary> summaries) {
        Map<String, Long> summaryMap = new ConcurrentHashMap<>();
        summaries.forEach(intSummary -> {
            summaryMap.put(ENGAGEMENT, summaryMap.getOrDefault(ENGAGEMENT, 0L) + intSummary.engagement());
            summaryMap.put(IMPRESSIONS, summaryMap.getOrDefault(IMPRESSIONS, 0L) + intSummary.impressions());
            summaryMap.put(REACH, summaryMap.getOrDefault(REACH, 0L) + intSummary.reach());
            summaryMap.put(COMMENTS, summaryMap.getOrDefault(COMMENTS, 0L) + intSummary.comments());
            summaryMap.put(LIKES, summaryMap.getOrDefault(LIKES, 0L) + intSummary.likes());
            summaryMap.put(SHARED, summaryMap.getOrDefault(SHARED, 0L) + intSummary.shared());
        });
        return InteractionSummary.builder()
                .engagement(summaryMap.get(ENGAGEMENT))
                .impressions(summaryMap.get(IMPRESSIONS))
                .reach(summaryMap.get(REACH))
                .comments(summaryMap.get(COMMENTS))
                .likes(summaryMap.get(LIKES))
                .shared(summaryMap.get(SHARED))
                .build();
    }

    private Map<String, Long> newInsightMap(InteractionSummary summary) {
        Map<String, Long> metricMap = new ConcurrentHashMap<>();
        metricMap.put(ENGAGEMENT, summary.engagement());
        metricMap.put(IMPRESSIONS, summary.impressions());
        metricMap.put(REACH, summary.reach());
        metricMap.put(LIKES, summary.likes());
        metricMap.put(COMMENTS, summary.comments());
        metricMap.put(SHARED, summary.shared());
        return metricMap;
    }
}
