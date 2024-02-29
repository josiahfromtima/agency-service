package com.tima.platform.service.dashboard;

import com.tima.platform.converter.CampaignRegistrationConverter;
import com.tima.platform.converter.ClientSocialMediaConverter;
import com.tima.platform.domain.ClientSocialMedia;
import com.tima.platform.domain.InfluencerApplication;
import com.tima.platform.model.api.request.ClientSelectedSocialMedia;
import com.tima.platform.model.api.response.instagram.business.BasicBusinessInsight;
import com.tima.platform.repository.CampaignRegistrationRepository;
import com.tima.platform.repository.ClientSocialMediaRepository;
import com.tima.platform.repository.InfluencerApplicationRepository;
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

import java.util.*;

import static com.tima.platform.util.AppUtil.safeCastList;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/21/24
 */
@Service
@RequiredArgsConstructor
public class CampaignAnalyticsKpiService {
    private final LoggerHelper log = LoggerHelper.newInstance(CampaignAnalyticsKpiService.class.getName());
    private final InfluencerApplicationRepository applicationRepository;
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


    public Mono<List<String>> getAccounts(String publicId) {
        log.info("Get Accounts on campaign ");
        return getInfluencers(publicId);
    }
    public Mono<Long> getFollowers(String publicId) {
        log.info("Get Followers on campaign ");
        return getInfluencers(publicId)
                .flatMap(accounts -> buildFollowers(accounts, publicId))
                .map(this::calculateTotalFollowers);
    }

    public Mono<List<String>> getInfluencers(String publicId) {
        return applicationRepository.findByCampaignPublicId(publicId)
                .flatMap(this::getInfluencerId)
                .collectList()
                .map(this::getDistinctAccounts);
    }


    private List<String> getDistinctAccounts(List<String> accounts) {
        Set<String> uniqueAccount = new HashSet<>(accounts);
        return uniqueAccount.stream().toList();
    }

    private long calculateTotalFollowers(List<?> basicInsights) {
        return safeCastList(basicInsights, BasicBusinessInsight.class).stream()
                .map(BasicBusinessInsight::followers)
                .reduce(0L, Long::sum);
    }

    private Mono<String> getInfluencerId(InfluencerApplication application) {
        return Mono.just(application.getSubmittedBy());
    }

    private Mono<List<?>> buildFollowers(List<String> accounts, String publicId) {
        return getSocialMedia(publicId)
                .flatMap(names ->  processMedia(names, accounts).collectList() );
    }

    private Flux<?> processMedia(List<String> names, List<String> accounts) {
        return Flux.fromIterable(names)
                .flatMap(name ->  Flux.fromIterable(accounts)
                        .flatMap(account -> socialMediaRepository.findByName(name)
                                .flatMap(socialMedia -> mediaRepository.findByUserId(account)
                                        .flatMap(clientSocialMedia ->
                                                socialMediaInsights.getOrDefault(name, socialBusinessInsight)
                                                .getUserBasicBusinessInsight(getSelectedMedia(name, clientSocialMedia),
                                                        socialMedia.getAccessToken())
                                        )
                                )
                        )
                );
    }

    public Mono<List<String>> getSocialMedia(String publicId) {
        return registrationRepository.findByPublicId(publicId)
                .map(CampaignRegistrationConverter::mapToRecord)
                .map(registration -> registration.overview().socialMediaPlatforms());
    }

    public ClientSelectedSocialMedia getSelectedMedia(String name, ClientSocialMedia media) {
        log.info("Getting selected media for analytics");
        List<ClientSelectedSocialMedia> mediaList = ClientSocialMediaConverter.mapToRecord(media).selectedSocialMedia();
        return mediaList.stream().filter(media1 -> media1.name().equals(name))
                .findFirst()
                .orElse(ClientSelectedSocialMedia.builder().build());
    }

}
