package com.tima.platform.service;

import com.tima.platform.converter.CampaignCreativeConverter;
import com.tima.platform.domain.CampaignCreative;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.response.CampaignCreativeRecord;
import com.tima.platform.repository.CampaignCreativeRepository;
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
public class CampaignCreativeService {
    private final LoggerHelper log = LoggerHelper.newInstance(CampaignCreativeService.class.getName());
    private final CampaignCreativeRepository creativeRepository;

    private static final String CAMPAIGN_MSG = "Campaign request executed successfully";
    private static final String ERROR_MSG = "The campaign record mutation could not be performed";

    public Mono<AppResponse> getCampaignCreatives() {
        log.info("Getting ALl Campaign Creative Records...");
        return creativeRepository.findAll()
                .collectList()
                .map(CampaignCreativeConverter::mapToRecords)
                .map(campaignRecords -> AppUtil.buildAppResponse(campaignRecords, CAMPAIGN_MSG));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> addOrUpdateCampaignCreative(CampaignCreativeRecord audienceRecord) {
        log.info("Add New Campaign Creative Record...");
        return getCampaignCreative()
                .flatMap(campaignCreative -> {
                    CampaignCreative existingRecords = CampaignCreativeConverter.mapToEntity(audienceRecord);
                    existingRecords.setId(campaignCreative.getId());
                    return creativeRepository.save(existingRecords);
                })
                .switchIfEmpty(creativeRepository.save(CampaignCreativeConverter.mapToEntity(audienceRecord)))
                .map(CampaignCreativeConverter::mapToRecord)
                .map(campaignRecord -> AppUtil.buildAppResponse(campaignRecord, CAMPAIGN_MSG))
                .onErrorResume(throwable ->
                        handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()) );
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> deleteCampaignCreative(String publicId) {
        log.info("Deleting Campaign Creative Record...", publicId);
        return getCampaignCreative()
                .flatMap(creativeRepository::delete)
                .then(Mono.fromCallable(() ->
                        AppUtil.buildAppResponse("Campaign Creative Deleted", CAMPAIGN_MSG)));
    }

    private Mono<CampaignCreative> getCampaignCreative() {
        log.info("Getting Existing New Campaign Creative Record...");
        return creativeRepository
                .findAll()
                .collectList()
                .flatMap(campaignCreatives -> campaignCreatives.isEmpty() ?
                        Mono.empty() : Mono.just(campaignCreatives.get(0))
                );
    }
}
