package com.tima.platform.service.search;

import com.google.gson.reflect.TypeToken;
import com.tima.platform.converter.InfluencerApplicationConverter;
import com.tima.platform.domain.CampaignRegistration;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.InfluencerRecord;
import com.tima.platform.model.api.response.FullUserProfileRecord;
import com.tima.platform.repository.CampaignRegistrationRepository;
import com.tima.platform.repository.InfluencerApplicationRepository;
import com.tima.platform.service.helper.UserProfileService;
import com.tima.platform.util.InfluencerSearchSetting;
import com.tima.platform.util.LoggerHelper;
import com.tima.platform.util.ReportSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND;
import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND_INFLUENCER;
import static com.tima.platform.util.AppUtil.buildAppResponse;
import static com.tima.platform.util.AppUtil.gsonInstance;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/8/24
 */
@Service
@RequiredArgsConstructor
public class InfluencerSearchService {
    private final LoggerHelper log = LoggerHelper.newInstance(InfluencerSearchService.class.getName());
    private final InfluencerApplicationRepository applicationRepository;
    private final CampaignRegistrationRepository registrationRepository;
    private final UserProfileService profileService;

    private static final String SEARCH_MSG = "Bookmark request executed successfully";
    private static final int DAYS = 7;
    private static final short COMPLETED = 100;
    private static final String COST_POST = "costPerPost";

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> getTopCategories() {
        log.info("Getting Top Categories");
        Instant daysAgo = Instant.now().minus(DAYS, ChronoUnit.DAYS);
        return registrationRepository.findByCreatedOnAfter(daysAgo, setPageProperty() )
                .flatMap(this::getCategories)
                .collectList()
                .flatMap(this::process)
                .map(strings -> buildAppResponse(strings, SEARCH_MSG));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> getInfluencers(String category) {
        log.info("Getting Influencer by Category ", category);
        return registrationRepository.getRecommendedCampaign(getParam(category), "","" )
                .flatMap(registration -> applicationRepository.findByCampaignPublicId(registration.getPublicId()))
                .map(InfluencerApplicationConverter::mapToInfluencer)
                .collectList()
                .flatMap(this::getUniqueRecords)
                .map(strings -> buildAppResponse(strings, SEARCH_MSG));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> getTopInfluencers(ReportSettings settings) {
        log.info("Getting Top Influencers by Category ");
        Instant daysAgo = Instant.now().minus(DAYS, ChronoUnit.DAYS);
        settings.setSortBy(COST_POST);
        settings.setSortIn("desc");
        return registrationRepository.findByCreatedOnAfter(daysAgo, setPage(settings))
                .flatMap(registration -> applicationRepository.findByCampaignPublicId(registration.getPublicId()))
                .map(InfluencerApplicationConverter::mapToInfluencer)
                .collectList()
                .flatMap(this::getUniqueRecords)
                .map(strings -> buildAppResponse(strings, SEARCH_MSG));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> getNewInfluencers(String token) {
        Instant daysAgo = Instant.now().minus(DAYS, ChronoUnit.DAYS);
        log.info("Getting New Influencers For one week ", daysAgo);
        return profileService.getUserProfiles(token)
                .map(profileRecords -> profileRecords.stream()
                        .filter(userProfileRecord -> filter(userProfileRecord, daysAgo))
                        .map(this::convertFrom)
                        .toList())
                .map(profiles -> buildAppResponse(profiles, SEARCH_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getInfluencer(String publicId, String token) {
        log.info("Getting Influencer Information ", publicId);
        return profileService.getUserProfile(token, publicId)
                .flatMap(userProfileRecord -> getCompletedJobs(publicId, userProfileRecord))
                .map(profiles -> buildAppResponse(profiles, SEARCH_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getInfluencerByFilter(String token, InfluencerSearchSetting setting) {
        log.info("Getting Influencer with Filter ", setting);
        return profileService.getUserProfiles(token)
                .flatMap(profileRecords -> filter(profileRecords, setting))
                .map(profiles -> buildAppResponse(profiles, SEARCH_MSG));
    }


    private Mono<InfluencerRecord> getCompletedJobs(String publicId, FullUserProfileRecord profileRecord) {
        return applicationRepository.findBySubmittedBy(publicId)
                .flatMap(application ->
                        registrationRepository.findByStatusAndPublicId(COMPLETED, application.getCampaignPublicId()))
                .collectList()
                .map(campaignRegistrations -> convertFrom(profileRecord, campaignRegistrations.size()))
                .switchIfEmpty(Mono.just(convertFrom(profileRecord, 0)));
    }

    private Mono<List<String>> getCategories(CampaignRegistration registration) {
        return Mono.just(gsonInstance()
                .fromJson(registration.getInfluencerCategory(), new TypeToken<List<String>>(){}.getType()) );
    }

    private Mono<List<String>> process(List<List<String>> categoriesList) {
        Map<String, Integer> categories = new ConcurrentHashMap<>();
        categoriesList
                .forEach(strings -> strings
                        .forEach(s -> categories.put(s, categories.getOrDefault(s, 0) + 1)));

        List<Map.Entry<String, Integer>> entries
                = categories.entrySet()
                .stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue())).toList();

        log.info("entries ", entries);

        return Mono.just(entries.stream().map(Map.Entry::getKey).toList());
    }

    private Mono<List<InfluencerRecord>> filter(List<FullUserProfileRecord> profileRecords,
                                                InfluencerSearchSetting setting) {
        return Mono.just(
                profileRecords.stream()
                .filter(profile -> profile.username().equalsIgnoreCase(setting.getUsername()) ||
                        profile.profile().email().equalsIgnoreCase(setting.getEmail()) ||
                        profile.profile().firstName().equalsIgnoreCase(setting.getName()) ||
                        profile.profile().lastName().equalsIgnoreCase(setting.getName()))
                .map(this::convertFrom)
                .toList()
        );
    }

    private Mono<List<InfluencerRecord>> getUniqueRecords(List<InfluencerRecord> recordList) {
        Map<String, InfluencerRecord> influencerRecordMap = new ConcurrentHashMap<>();
        recordList.forEach(influencerRecord -> influencerRecordMap.put(influencerRecord.publicId(), influencerRecord));
        return Mono.just(influencerRecordMap.values().stream().toList());
    }

    private InfluencerRecord convertFrom(FullUserProfileRecord profileRecord) {
        return InfluencerRecord.builder()
                .publicId(profileRecord.publicId())
                .username(profileRecord.username())
                .fullName(profileRecord.profile().firstName()+" "+profileRecord.profile().lastName())
                .email(profileRecord.profile().email())
                .phoneNumber(profileRecord.profile().phoneNumber())
                .profilePicture(profileRecord.profile().profilePicture())
                .build();
    }

    private InfluencerRecord convertFrom(FullUserProfileRecord profileRecord, Integer completed) {
        return InfluencerRecord.builder()
                .publicId(profileRecord.publicId())
                .username(profileRecord.username())
                .fullName(profileRecord.profile().firstName()+" "+profileRecord.profile().lastName())
                .email(profileRecord.profile().email())
                .phoneNumber(profileRecord.profile().phoneNumber())
                .profilePicture(profileRecord.profile().profilePicture())
                .completed(completed)
                .build();
    }

    private boolean filter(FullUserProfileRecord profileRecord, Instant criteria) {
        return ReportSettings
                .instance()
                .toInstant(profileRecord.profile().createdOn())
                .getEnd().isAfter(criteria);
    }

    private Pageable setPageProperty() {
        return setPage(ReportSettings
                .instance()
                .page(0)
                .size(10)
                .sortBy(COST_POST)
                .sortIn("desc"));
    }

    private Pageable setPage(ReportSettings settings) {
        return PageRequest.of(settings.getPage(), settings.getSize(),
                Sort.Direction.fromString(settings.getSortIn()), settings.getSortBy());
    }

    private String getParam(String item) {
        return "%"+item.toLowerCase()+"%";
    }
}
