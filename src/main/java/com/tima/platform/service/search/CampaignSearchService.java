package com.tima.platform.service.search;

import com.tima.platform.converter.CampaignRegistrationConverter;
import com.tima.platform.converter.InfluencerApplicationConverter;
import com.tima.platform.domain.CampaignRegistration;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.response.InfluencerApplicationRecord;
import com.tima.platform.repository.CampaignRegistrationRepository;
import com.tima.platform.repository.InfluencerApplicationRepository;
import com.tima.platform.service.helper.InfluencersInsightService;
import com.tima.platform.util.CampaignSearchSetting;
import com.tima.platform.util.LoggerHelper;
import com.tima.platform.util.ReportSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND_INFLUENCER;
import static com.tima.platform.util.AppUtil.buildAppResponse;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/29/24
 */
@Service
@RequiredArgsConstructor
public class CampaignSearchService {
    private final LoggerHelper log = LoggerHelper.newInstance(CampaignSearchService.class.getName());
    private final InfluencerApplicationRepository applicationRepository;
    private final CampaignRegistrationRepository registrationRepository;
    private final InfluencersInsightService insightService;

    private static final String SEARCH_MSG = "Campaign Search executed successfully";

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getInfluencerCampaignSearch(CampaignSearchSetting setting) {
        log.info("Getting Influencer For Campaign Search with Filter ", setting);
        return getCampaignSearch(setting)
                .flatMap(registration -> applicationRepository.findByCampaignPublicId(registration.getPublicId()))
                .collectList()
                .map(InfluencerApplicationConverter::mapToRecords)
                .flatMap(this::getUniqueRInfluencers)
                .map(this::convertToFlux)
                .flatMap(applicationFlux -> applicationFlux.flatMap(insightService::getInfluencers).collectList() )
                .map(profiles -> buildAppResponse(profiles, SEARCH_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getPastCampaigns(String publicId, ReportSettings settings)  {
        log.info("Getting Influencer Past Experiences ", settings);
        short completedStatus = 100;
        return  applicationRepository.findBySubmittedBy(publicId, setPage(settings))
                .flatMap(application -> registrationRepository
                        .findByPublicIdAndStatus(application.getCampaignPublicId(), completedStatus))
                .collectList()
                .map(CampaignRegistrationConverter::mapToExperienceRecords)
                .map(profiles -> buildAppResponse(profiles, SEARCH_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getCampaignsByName(String name, ReportSettings settings)  {
        log.info("Getting Campaign by name search ", settings);
        return  registrationRepository.findByNameContainingIgnoreCase(name, setPage(settings))
                .collectList()
                .map(CampaignRegistrationConverter::mapToSearchRecords)
                .map(profiles -> buildAppResponse(profiles, SEARCH_MSG));
    }



    private Flux<CampaignRegistration> getCampaignSearch(CampaignSearchSetting setting) {
        log.info("Searching for Campaign influencer with filter ", setting);
        setting.setCategory(getParam(setting.getCategory()));
        setting.setAudienceAge(getParam(setting.getAudienceAge()));
        setting.setAudienceSize(getParam(setting.getAudienceSize()));
        setting.setAudienceLocation(getParam(setting.getAudienceLocation()));
        setting.setAudienceGender(getParam(setting.getAudienceGender()));
        setting.setSocialMediaPlatform(getParam(setting.getSocialMediaPlatform()));
        return registrationRepository.getSearchResult2(setting.getCategory(), setting.getAudienceSize(),
                setting.getAudienceAge(), setting.getAudienceLocation(), setting.getAudienceGender(),
                setting.getSocialMediaPlatform(), BigDecimal.valueOf(setting.getCostPerPost() ));
    }

    private Mono<List<InfluencerApplicationRecord>> getUniqueRInfluencers(List<InfluencerApplicationRecord> recordList) {
        Map<String, InfluencerApplicationRecord> influencerRecordMap = new ConcurrentHashMap<>();
        recordList.forEach(influencerRecord -> influencerRecordMap.put(influencerRecord.submittedBy(), influencerRecord));
        return Mono.just(influencerRecordMap.values().stream().toList());
    }
    private Flux<InfluencerApplicationRecord> convertToFlux(List<InfluencerApplicationRecord> recordList) {
        return Flux.fromIterable(recordList);
    }

    private Pageable setPage(ReportSettings settings) {
        return PageRequest.of(settings.getPage(), settings.getSize(),
                Sort.Direction.fromString(settings.getSortIn()), settings.getSortBy());
    }

    private String getParam(String item) {
        return "%"+item.toLowerCase()+"%";
    }
}
