package com.tima.platform.service;

import com.tima.platform.converter.InfluencerApplicationConverter;
import com.tima.platform.domain.CampaignRegistration;
import com.tima.platform.domain.InfluencerApplication;
import com.tima.platform.event.AlertEvent;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.AlertRecord;
import com.tima.platform.model.api.request.UserCampaignRecord;
import com.tima.platform.model.api.response.InfluencerApplicationRecord;
import com.tima.platform.model.constant.AlertType;
import com.tima.platform.model.constant.ApplicationStatus;
import com.tima.platform.repository.CampaignRegistrationRepository;
import com.tima.platform.repository.InfluencerApplicationRepository;
import com.tima.platform.service.helper.InfluencersInsightService;
import com.tima.platform.service.helper.UserProfileService;
import com.tima.platform.util.AppError;
import com.tima.platform.util.AppUtil;
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

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/21/23
 */
@Service
@RequiredArgsConstructor
public class InfluencerApplicationService {
    private final LoggerHelper log = LoggerHelper.newInstance(InfluencerApplicationService.class.getName());
    private final InfluencerApplicationRepository applicationRepository;
    private final CampaignRegistrationRepository registrationRepository;
    private final UserProfileService userProfileService;
    private final InfluencersInsightService insightService;
    private final AlertEvent alertEvent;

    private static final String APPLICATION_MSG = "Application request executed successfully";
    private static final String INVALID_APPLICATION = "The application id is invalid";
    private static final String INVALID_CAMPAIGN = "The campaign selected is invalid";
    private static final String INVALID_DATE = "Invalid Date format. Accepted format is YYYY-MM-DD";
    private static final String INVALID_STATUS = "Invalid Application status provided";

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getApplicationStatuses() {
        log.info("Getting ALl Application Status...");
        return Mono.just(AppUtil.buildAppResponse(ApplicationStatus.values(), APPLICATION_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getApplications(ReportSettings settings) {
        log.info("Getting ALl Influencer Application Records...");
        return applicationRepository.findAllBy(setPage(settings))
                .flatMap(this::addBanner)
                .collectList()
                .map(InfluencerApplicationConverter::mapToRecords)
                .map(applicationRecords -> AppUtil.buildAppResponse(applicationRecords, APPLICATION_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getApplication(String applicationId) {
        log.info("Getting Influencer Application Record by ", applicationId);
        return getApplicationById(applicationId)
                .flatMap(this::addBanner)
                .map(InfluencerApplicationConverter::mapToRecord)
                .map(applicationRecord -> AppUtil.buildAppResponse(applicationRecord, APPLICATION_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getApplicationsByStatus(String appStatus, ReportSettings settings) {
        log.info("Getting Influencer Application Records by ", appStatus);
        ApplicationStatus status = parseStatus(appStatus);
        if(Objects.isNull(status)) return handleOnErrorResume(new AppException(INVALID_STATUS), BAD_REQUEST.value());
        return applicationRepository.findByStatus(status, setPage(settings))
                .flatMap(this::addBanner)
                .collectList()
                .map(InfluencerApplicationConverter::mapToRecords)
                .map(applicationRecords -> AppUtil.buildAppResponse(applicationRecords, APPLICATION_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getApplicationsByStatusAndCampaign(String appStatus,
                                                                String campaignId,
                                                                ReportSettings settings) {
        log.info(String.format("Getting Influencer Application Records by %s and %s", appStatus, campaignId));
        ApplicationStatus status = parseStatus(appStatus);
        if(Objects.isNull(status)) return handleOnErrorResume(new AppException(INVALID_STATUS), BAD_REQUEST.value());
        Flux<InfluencerApplicationRecord> applicationRecordsFlux
                = applicationRepository.findByStatusAndCampaignPublicId(status, campaignId, setPage(settings))
                .flatMap(this::addBanner)
                .map(InfluencerApplicationConverter::mapToRecord);
        return (status.equals(ApplicationStatus.APPROVED)) ?
                applicationRecordsFlux
                        .flatMap(insightService::getInfluencers)
                        .collectList()
                        .map(applicationRecords -> AppUtil.buildAppResponse(applicationRecords, APPLICATION_MSG)) :
                applicationRecordsFlux
                        .collectList()
                        .map(applicationRecords -> AppUtil.buildAppResponse(applicationRecords, APPLICATION_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getApplicationsByCampaignId(String publicId, ReportSettings settings) {
        log.info("Getting Influencer Application Records by campaign ", publicId);
        return getCampaign(publicId)
                .flatMap(campaign -> applicationRepository
                        .findByCampaignId(campaign.getId(), setPage(settings))
                        .flatMap(this::addBanner)
                        .collectList() )
                .map(InfluencerApplicationConverter::mapToRecords)
                .map(applicationRecords -> AppUtil.buildAppResponse(applicationRecords, APPLICATION_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getApplicationsByApplicant(String publicId, ReportSettings settings) {
        log.info("Getting Influencer Application Records by Applicant (", publicId, ")");
        return applicationRepository.findBySubmittedBy(publicId, setPage(settings))
                .flatMap(this::addBanner)
                .collectList()
                .map(InfluencerApplicationConverter::mapToRecords)
                .map(applicationRecords -> AppUtil.buildAppResponse(applicationRecords, APPLICATION_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getApplicationsByDateRange(String begin,
                                                        String end,
                                                        ReportSettings settings) {
        log.info("Getting Influencer Application Records from ", begin, " to ", end);
        LocalDate startDate = parseDate(begin);
        LocalDate endDate = parseDate(end);
        if(Objects.isNull(startDate) || Objects.isNull(endDate)) {
            return handleOnErrorResume(new AppException(INVALID_DATE), BAD_REQUEST.value());
        }
        return applicationRepository.findByApplicationDateBetween(startDate, endDate, setPage(settings))
                .flatMap(this::addBanner)
                .collectList()
                .map(InfluencerApplicationConverter::mapToRecords)
                .map(applicationRecords -> AppUtil.buildAppResponse(applicationRecords, APPLICATION_MSG));
    }

    @PreAuthorize(ADMIN_INFLUENCER)
    public Mono<AppResponse> addApplication(InfluencerApplicationRecord appRecord, String publicId, String token) {
        log.info("Saving Influencer Application Record ", publicId);
        return aggregate(appRecord.campaignPublicId(), token)
                .doOnNext(userCampaign -> log.info("Got User Campaign"))
                .flatMap(userCampaign -> {
                    InfluencerApplication newRecord = InfluencerApplicationConverter.mapToEntity(appRecord);
                    newRecord.setCampaignId(userCampaign.campaign().getId());
                    newRecord.setCampaignName(userCampaign.campaign().getName());
                    newRecord.setCampaignBudget(userCampaign.campaign().getPlannedBudget());
                    newRecord.setCampaignDescription(userCampaign.campaign().getBriefDescription());
                    newRecord.setUsername(userCampaign.userProfileRecord().username());
                    newRecord.setEmail(userCampaign.userProfileRecord().profile().email());
                    newRecord.setFullName(userCampaign.userProfileRecord().profile().firstName() +" "+
                            userCampaign.userProfileRecord().profile().lastName());
                    newRecord.setPhoneNumber(userCampaign.userProfileRecord().profile().phoneNumber());
                    newRecord.setProfilePicture(userCampaign.userProfileRecord().profile().profilePicture());
                    newRecord.setSocialMediaPlatform(userCampaign.campaign().getSocialMediaPlatforms());
                    newRecord.setSubmittedBy(publicId);
                    return applicationRepository.save(newRecord);
                })
                .doOnNext(application -> log.info("Preparing to notice user"))
                .flatMap(application -> sendAlert(application, AlertType.NEW))
                .map(InfluencerApplicationConverter::mapToRecord)
                .map(applicationRecord -> AppUtil.buildAppResponse(applicationRecord, APPLICATION_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.message(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_BRAND)
    public Mono<AppResponse> reviewApplication(String appId,
                                               String publicId,
                                               String reviewStatus) {
        log.info("Updating Reviewed Application Record ", reviewStatus);
        ApplicationStatus status = parseStatus(reviewStatus);
        if(Objects.isNull(status)) return handleOnErrorResume(new AppException(INVALID_STATUS), BAD_REQUEST.value());
        return getApplicationById(appId)
                .flatMap(application -> {
                    application.setStatus(status);
                    application.setReviewedBy(publicId);
                    application.setEditedOn(Instant.now());
                    if(ApplicationStatus.APPROVED == status)  application.setApprovedBy(publicId);
                    return applicationRepository.save(application);
                })
                .flatMap(this::addBanner)
                .flatMap(application -> sendAlert(application, AlertType.REVIEW))
                .map(InfluencerApplicationConverter::mapToRecord)
                .map(applicationRecord -> AppUtil.buildAppResponse(applicationRecord, APPLICATION_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.message(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> deleteApplication(String applicationId) {
        log.info("Deleting Influencer Application Record for ", applicationId);
        return getApplicationById(applicationId)
                .flatMap(applicationRepository::delete)
                .then(Mono.fromCallable(() ->
                        AppUtil.buildAppResponse("Application "+ applicationId + " Deleted", APPLICATION_MSG)));
    }

    private Mono<CampaignRegistration> getCampaign(String publicId) {
        return registrationRepository.findByPublicId(publicId)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_CAMPAIGN), BAD_REQUEST.value()));
    }
    private Mono<InfluencerApplication> getApplicationById(String applicationId) {
        return applicationRepository.findByApplicationId(applicationId)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_APPLICATION), BAD_REQUEST.value()));
    }

    private Mono<UserCampaignRecord> aggregate(String publicId, String token) {
        return getCampaign(publicId)
                .flatMap(campaignRegistration -> userProfileService.getUserProfile(token)
                        .doOnNext(userProfileRecord -> log.info("Got user profile for brand"))
                        .map(userProfileRecord -> UserCampaignRecord.builder()
                                .campaign(campaignRegistration)
                                .userProfileRecord(userProfileRecord)
                                .build())
                ).doOnNext(log::info);
    }

    private Mono<InfluencerApplication> addBanner(InfluencerApplication application) {
        return registrationRepository.findByPublicId(application.getCampaignPublicId())
                .map(registration ->  {
                    application.setCampaignLogo(registration.getThumbnail());
                    return application;
                });
    }

    private Mono<InfluencerApplication> sendAlert(InfluencerApplication application, AlertType alertType) {
        return registrationRepository.findByPublicId(application.getCampaignPublicId())
                        .flatMap(registration -> alertEvent.registerAlert(AlertRecord.builder()
                                .userPublicId(AlertType.NEW.name().equals(alertType.name()) ?
                                        registration.getCreatedBy() : application.getSubmittedBy())
                                .title(alertType.getTitle(alertType.name(), application.getCampaignName()))
                                .message(alertType.getMessage(
                                        alertType.getType(),
                                        application.getCampaignName(),
                                        application.getApplicationId()))
                                .type(AlertType.CAMPAIGN.name())
                                .typeStatus("")
                                .status("NEW")
                                .build()))
                .map(aBoolean -> application);
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        }catch (Exception e) {
            return null;
        }
    }
    private ApplicationStatus parseStatus(String status) {
        try {
            return ApplicationStatus.valueOf(status);
        }catch (Exception e) {
            return null;
        }
    }

    private Pageable setPage(ReportSettings settings) {
        return PageRequest.of(settings.getPage(), settings.getSize(),
                Sort.Direction.fromString(settings.getSortIn()), settings.getSortBy());
    }
}
