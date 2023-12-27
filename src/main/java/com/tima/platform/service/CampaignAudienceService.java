package com.tima.platform.service;

import com.tima.platform.converter.CampaignAudienceConverter;
import com.tima.platform.domain.CampaignAudience;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.response.CampaignAudienceRecord;
import com.tima.platform.repository.CampaignAudienceRepository;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.ADMIN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Service
@RequiredArgsConstructor
public class CampaignAudienceService {
    private final LoggerHelper log = LoggerHelper.newInstance(CampaignAudienceService.class.getName());
    private final CampaignAudienceRepository audienceRepository;

    private static final String CAMPAIGN_MSG = "Campaign request executed successfully";
    private static final String ERROR_MSG = "The campaign record mutation could not be performed";

    public Mono<AppResponse> getCampaignAudiences() {
        log.info("Getting ALl Campaign Audience Records...");
        return audienceRepository.findAll()
                .collectList()
                .map(CampaignAudienceConverter::mapToRecords)
                .map(campaignRecords -> AppUtil.buildAppResponse(campaignRecords, CAMPAIGN_MSG));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> addOrUpdateCampaignAudience(CampaignAudienceRecord audienceRecord) {
        log.info("Add New Campaign Audience Record...");
        return getCampaignAudience()
                .flatMap(campaignAudience -> {
                    CampaignAudience existingRecords = CampaignAudienceConverter.mapToEntity(audienceRecord);
                    existingRecords.setId(campaignAudience.getId());
                    existingRecords.setCreatedOn(campaignAudience.getCreatedOn());
                    return audienceRepository.save(existingRecords);
                })
                .switchIfEmpty(audienceRepository.save(CampaignAudienceConverter.mapToEntity(audienceRecord)))
                .map(CampaignAudienceConverter::mapToRecord)
                .map(campaignRecord -> AppUtil.buildAppResponse(campaignRecord, CAMPAIGN_MSG))
                .onErrorResume(throwable ->
                        handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()) );
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> deleteCampaignAudience(String publicId) {
        log.info("Deleting Campaign Audience Record...", publicId);
        return getCampaignAudience()
                .flatMap(audienceRepository::delete)
                .then(Mono.fromCallable(() ->
                        AppUtil.buildAppResponse("Campaign Audience Deleted", CAMPAIGN_MSG)));
    }

    private Mono<CampaignAudience> getCampaignAudience() {
        log.info("Getting Existing New Campaign Audience Record...");
        return audienceRepository
                .findAll()
                .collectList()
                .flatMap(campaignAudiences -> campaignAudiences.isEmpty() ?
                        Mono.empty() : Mono.just(campaignAudiences.get(0))
                );
    }

}
