package com.tima.platform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.reflect.TypeToken;
import com.tima.platform.converter.CampaignRegistrationConverter;
import com.tima.platform.domain.CampaignRegistration;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.CampaignCreativeRecord;
import com.tima.platform.model.api.request.CampaignInfluencerRecord;
import com.tima.platform.model.api.request.CampaignOverviewRecord;
import com.tima.platform.model.api.response.CampaignRegistrationRecord;
import com.tima.platform.model.constant.RegistrationType;
import com.tima.platform.repository.CampaignRegistrationRepository;
import com.tima.platform.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND;
import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND_INFLUENCER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/20/23
 */
@Service
@RequiredArgsConstructor
public class CampaignRegistrationService {
    private final LoggerHelper log = LoggerHelper.newInstance(CampaignRegistrationService.class.getName());
    private final CampaignRegistrationRepository registrationRepository;

    @Value("${aws.s3.url}")
    private String baseResourceUrl;
    @Value("${aws.s3.resource.thumbnail}")
    private String thumbnailFolder;
    @Value("${aws.s3.image-ext}")
    private String defaultFileExtension;

    private static final String CAMPAIGN_MSG = "Campaign request executed successfully";
    private static final String INVALID_CAMPAIGN = "Selected campaign is not found";
    private static final String ERROR_MSG = "The campaign record mutation could not be performed";

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getRegistrationTypes() {
        log.info("Getting ALl Campaign Registration Types...");
        return Mono.just(AppUtil.buildAppResponse(
                Arrays.stream(RegistrationType.values()).toList(),
                CAMPAIGN_MSG
        ));
    }
    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getCampaignRegistrations(ReportSettings settings) {
        log.info("Getting ALl Campaign Registration Records...");
        return registrationRepository.findAllBy(setPage(settings))
                .collectList()
                .map(CampaignRegistrationConverter::mapToRecords)
                .map(campaignRecords -> AppUtil.buildAppResponse(campaignRecords, CAMPAIGN_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getCampaignRegistration(String publicId) {
        log.info("Getting Campaign Registration Record for ", publicId);
        return getRegistration(publicId)
                .map(CampaignRegistrationConverter::mapToRecord)
                .map(campaignRecords -> AppUtil.buildAppResponse(campaignRecords, CAMPAIGN_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getCampaignRegistrationByBrand(String brand, ReportSettings settings) {
        log.info("Getting Campaign Registration Record by brand ", brand);
        return getRegistrationByBrand(brand, settings)
                .map(CampaignRegistrationConverter::mapToRecords)
                .map(campaignRecords -> AppUtil.buildAppResponse(campaignRecords, CAMPAIGN_MSG));
    }

    public Mono<AppResponse> getCampaigns(short status, String brandName, ReportSettings settings) {
        log.info("Getting Campaigns  Record ");
        return (Objects.isNull(brandName)) ? getCampaignRegistrationByStatus(status, settings) :
                getCampaignRegistrationByBrandAndStatus(brandName, status, settings);
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getCampaignRegistrationByStatus(short status, ReportSettings settings) {
        log.info("Getting Campaign Registration Record for ", status);
        return getRegistrationByStatusAndBrand(status, null, settings)
                .map(CampaignRegistrationConverter::mapToRecords)
                .map(campaignRecords -> AppUtil.buildAppResponse(campaignRecords, CAMPAIGN_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getCampaignRegistrationByBrandAndStatus(String brand, short status, ReportSettings settings) {
        log.info("Getting Campaign Registration Record for brand ", brand, " and ", status);
        return getRegistrationByStatusAndBrand(status, brand, settings)
                .map(CampaignRegistrationConverter::mapToRecords)
                .map(campaignRecords -> AppUtil.buildAppResponse(campaignRecords, CAMPAIGN_MSG));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> addCampaignRegistration(CampaignRegistrationRecord registrationRecord, String publicId) {
        log.info("Save full Campaign Registration Record ", registrationRecord);
        CampaignRegistration newRegistration = resolve(CampaignRegistrationConverter.mapToEntity(registrationRecord));
        newRegistration.setCreatedBy(publicId);
        return registrationRepository.save( newRegistration )
                .map(CampaignRegistrationConverter::mapToRecord)
                .map(campaignRecords -> AppUtil.buildAppResponse(campaignRecords, CAMPAIGN_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.message(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> addCampaignRegistration(JsonNode jsonNode, RegistrationType type, String publicId) {
        log.info("Save Part Campaign Registration Record ", jsonNode);
        if(!RegistrationType.OVERVIEW.equals(type) && jsonNode.at("/publicId").asText().isBlank()) {
            return handleOnErrorResume(new AppException(INVALID_CAMPAIGN), BAD_REQUEST.value());
        }else if(RegistrationType.OVERVIEW.equals(type)) {
            CampaignRegistration newRegistration
                    = CampaignRegistrationConverter.mapToEntity(registerPart(jsonNode, type));
            newRegistration.setCreatedBy(publicId);
            return registrationRepository.save(newRegistration)
                    .map(CampaignRegistrationConverter::mapToRecord)
                    .map(campaignRecords -> AppUtil.buildAppResponse(campaignRecords, CAMPAIGN_MSG))
                    .onErrorResume(t ->
                            handleOnErrorResume(new AppException(AppError.message(t.getMessage())), BAD_REQUEST.value()));
        }else {
            return getRegistration(jsonNode.at("/publicId").asText())
                    .flatMap(campaignRegistration -> {
                        CampaignRegistrationRecord registrationRecord = registerPart(jsonNode, type);
                        CampaignRegistrationRecord foundRecord
                                = CampaignRegistrationConverter.mapToRecord(campaignRegistration);
                        CampaignRegistration modified = resolve(CampaignRegistrationConverter.mapToEntity
                                (buildRecord(foundRecord, registrationRecord, type)) );
                        modified.setId(campaignRegistration.getId());
                        modified.setCreatedOn(campaignRegistration.getCreatedOn());
                        return registrationRepository.save(modified);
                    }).map(CampaignRegistrationConverter::mapToRecord)
                    .map(campaignRecords -> AppUtil.buildAppResponse(campaignRecords, CAMPAIGN_MSG))
                    .onErrorResume(t -> handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()));
        }
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> updateCampaignRegistration(CampaignRegistrationRecord registrationRecord) {
        log.info("Update full Campaign Registration Record ");
        return getRegistration(registrationRecord.publicId())
                .flatMap(campaignRegistration -> {
                    CampaignRegistration modified
                            = resolve(CampaignRegistrationConverter.mapToEntity(registrationRecord));
                    modified.setId(campaignRegistration.getId());
                    modified.setCreatedOn(campaignRegistration.getCreatedOn());
                    return registrationRepository.save(modified);
                }).map(CampaignRegistrationConverter::mapToRecord)
                .map(campaignRecords -> AppUtil.buildAppResponse(campaignRecords, CAMPAIGN_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.message(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> deleteCampaignRegistration(String publicId) {
        log.info("Delete Campaign Registration Record ");
        return getRegistration(publicId)
                .flatMap(registrationRepository::delete)
                .then(Mono.fromCallable(() -> AppUtil.buildAppResponse(publicId + " Deleted", CAMPAIGN_MSG)));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getRegistrationsBySearch(CampaignSearchSetting setting) {
        log.info("Get Search Campaign Registration Record ", setting);
        return getRegistrationSearch(setting)
                .collectList()
                .map(CampaignRegistrationConverter::mapToRecords)
                .map(campaignRecords -> AppUtil.buildAppResponse(campaignRecords, CAMPAIGN_MSG));
    }

    private Mono<CampaignRegistration> getRegistration(String publicId) {
        return registrationRepository.findByPublicId(publicId)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_CAMPAIGN), BAD_REQUEST.value()));
    }
    private Mono<List<CampaignRegistration>> getRegistrationByBrand(String brandName, ReportSettings settings) {
        return registrationRepository.findByBrandName(brandName, setPage(settings))
                .collectList()
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_CAMPAIGN), BAD_REQUEST.value()));
    }
    private Mono<List<CampaignRegistration>> getRegistrationByStatusAndBrand(short status,
                                                                             String brandName,
                                                                             ReportSettings settings) {
        return (Objects.isNull(brandName)) ?
                registrationRepository.findByStatusLessThanEqual(status, setPage(settings))
                .collectList()
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_CAMPAIGN), BAD_REQUEST.value())) :
                registrationRepository.findByBrandNameAndStatusLessThanEqual(brandName, status, setPage(settings))
                        .collectList()
                        .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_CAMPAIGN), BAD_REQUEST.value()));
    }

    private Flux<CampaignRegistration> getRegistrationSearch(CampaignSearchSetting setting) {
        log.info("Searchimg with filter ", setting);
        setting.setCategory(padSearchParam(setting.getCategory()));
        setting.setAudienceAge(padSearchParam(setting.getAudienceAge()));
        setting.setAudienceSize(padSearchParam(setting.getAudienceSize()));
        setting.setAudienceLocation(padSearchParam(setting.getAudienceLocation()));
        return registrationRepository.getSearchResult(setting.getCategory(), setting.getAudienceSize(),
                        setting.getAudienceAge(), setting.getAudienceLocation());
    }

    private CampaignRegistrationRecord registerPart(JsonNode jsonNode, RegistrationType type) {
        switch (type) {
            case OVERVIEW -> {
                    return CampaignRegistrationRecord.builder()
                                .overview(CampaignOverviewRecord.builder()
                                        .name(jsonNode.at("/name").asText())
                                        .briefDescription(jsonNode.at("/briefDescription").asText())
                                        .website(jsonNode.at("/website").asText())
                                        .plannedBudget(BigDecimal.valueOf(jsonNode.at("/plannedBudget").doubleValue()))
                                        .costPerPost(BigDecimal.valueOf(jsonNode.at("/costPerPost").doubleValue()))
                                        .socialMediaPlatforms(json(jsonNode.at("/socialMediaPlatforms").toString()) )
                                        .build())
                                .build();
            }
            case INFLUENCER -> {
                return CampaignRegistrationRecord.builder()
                            .influencer(CampaignInfluencerRecord.builder()
                                    .influencerCategory(json(jsonNode.at("/influencerCategory").toString()) )
                                    .audienceSize(json(jsonNode.at("/audienceSize").toString()) )
                                    .audienceGender(json(jsonNode.at("/audienceGender").toString()) )
                                    .audienceAgeGroup(json(jsonNode.at("/audienceAgeGroup").toString()) )
                                    .audienceLocation(json(jsonNode.at("/audienceLocation").toString()) )
                                    .build())
                            .build();
            }
            case CREATIVE -> {
                return CampaignRegistrationRecord.builder()
                        .creative(CampaignCreativeRecord.builder()
                                .paymentType(jsonNode.at("/paymentType").asText())
                                .startDate(localDate(jsonNode.at("/startDate").asText()))
                                .endDate(localDate(jsonNode.at("/endDate").asText()))
                                .contentType(jsonNode.at("/contentType").asText())
                                .contentPlacement(jsonNode.at("/contentPlacement").asText())
                                .creativeTone(jsonNode.at("/creativeTone").asText())
                                .creativeBrief(jsonNode.at("/creativeBrief").asText())
                                .rules(jsonNode.at("/rules").asText())
                                .referenceLink(jsonNode.at("/referenceLink").asText())
                                .awarenessObjective(json(jsonNode.at("/awarenessObjective").toString()))
                                .acquisitionObjective(json(jsonNode.at("/acquisitionObjective").toString()))
                                .thumbnail(jsonNode.at("/thumbnail").asText())
                                .visibility(jsonNode.at("/visibility").asBoolean())
                                .build())
                        .build();
            }
            default -> {
                return CampaignRegistrationRecord.builder().build();
            }
        }
    }

    private CampaignRegistrationRecord buildRecord(CampaignRegistrationRecord foundRecord,
                                                   CampaignRegistrationRecord updateRecord,
                                                   RegistrationType type) {
        if( RegistrationType.INFLUENCER.equals(type) ) {
            return CampaignRegistrationRecord.builder()
                    .publicId(foundRecord.publicId())
                    .overview(foundRecord.overview())
                    .influencer(updateRecord.influencer())
                    .creative(foundRecord.creative())
                    .build();
        }
        else if (RegistrationType.CREATIVE.equals(type)) {
            return CampaignRegistrationRecord.builder()
                    .publicId(foundRecord.publicId())
                    .overview(foundRecord.overview())
                    .influencer(foundRecord.influencer())
                    .creative(updateRecord.creative())
                    .build();
        }
        return foundRecord;
    }

    private CampaignRegistration resolve(CampaignRegistration registration) {
        registration.setThumbnail(resourceUrl(registration.getThumbnail(), thumbnailFolder));
        return registration;
    }

    private static List<String> json(String value) {
        return AppUtil.gsonInstance().fromJson(value, new TypeToken<List<String>>(){}.getType());
    }
    private static LocalDate localDate(String value) {
        return LocalDate.parse(value);
    }

    private String padSearchParam(String value) {
        return "%" + value.toLowerCase() + "%";
    }

    private String resourceUrl(String file, String folder) {
        return baseResourceUrl + folder + checkExt(file);
    }

    private String checkExt(String file) {
        if(file.contains(".jpeg") || file.contains(".jpg") || file.contains(".png")) return file;
        else return file + defaultFileExtension;
    }

    private Pageable setPage(ReportSettings settings) {
        return PageRequest.of(settings.getPage(), settings.getSize(),
                Sort.Direction.fromString(settings.getSortIn()), settings.getSortBy());
    }
}
