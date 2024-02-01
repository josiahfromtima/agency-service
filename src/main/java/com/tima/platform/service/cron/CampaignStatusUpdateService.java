package com.tima.platform.service.cron;

import com.tima.platform.domain.CampaignRegistration;
import com.tima.platform.event.AlertEvent;
import com.tima.platform.model.api.request.AlertRecord;
import com.tima.platform.model.constant.AlertType;
import com.tima.platform.model.constant.ApplicationStatus;
import com.tima.platform.repository.CampaignRegistrationRepository;
import com.tima.platform.repository.InfluencerApplicationRepository;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/26/23
 */
@Service
@RequiredArgsConstructor
public class CampaignStatusUpdateService {
    private final LoggerHelper log = LoggerHelper.newInstance(CampaignStatusUpdateService.class.getName());
    private final CampaignRegistrationRepository registrationRepository;
    private final InfluencerApplicationRepository applicationRepository;
    private final AlertEvent alertEvent;

    @Scheduled(cron = "${app.scheduler.campaign.status}")
    public void runCampaignStatus() {
        log.info("Running Campaign Status Update Job....");
        getValidCampaigns()
                .flatMap(this::getDateDifference)
                .collectList()
                .map(registrationRepository::saveAll)
                .flatMap(Flux::collectList)
                .subscribe(log::info);
    }

    private Flux<CampaignRegistration> getValidCampaigns() {
        log.info("Getting validate campaign for ", LocalDate.now());
        return registrationRepository.findByEndDateAfterOrEndDate(LocalDate.now(), LocalDate.now());
    }

    private Mono<CampaignRegistration> getDateDifference(CampaignRegistration registration) {
        LocalDate today = LocalDate.now();
        long noOfDays = registration.getEndDate().toEpochDay() - registration.getStartDate().toEpochDay();
        long daysFromToday = today.toEpochDay() - registration.getStartDate().toEpochDay();
        return processReport(registration, today, noOfDays, daysFromToday);
    }

    private Mono<CampaignRegistration> processReport(CampaignRegistration registration,
                                                     LocalDate date,
                                                     long noOfDays,
                                                     long remainingDays) {
        long percentage = (long) (((double) remainingDays/ noOfDays) * 100);
         if(registration.getStartDate().isEqual(date))
             return sendToStakeholders(registration, AlertType.STARTED, 0L);
         else if(registration.getStartDate().isAfter(date)) {
             registration.setStatus((short) 0);
             return Mono.just(registration);
         }else if(registration.getStartDate().isBefore(date) && registration.getStatus() != 100) {
             registration.setStatus((short) percentage);
             return ((percentage >= 25 && percentage <= 30) || (percentage >= 50 && percentage <= 55) ||
                     (percentage >= 75 && percentage <= 80)) ?
             sendToStakeholders(registration, AlertType.PROGRESS, percentage) : Mono.just(registration);
         }else if(registration.getEndDate().isEqual(date)) {
             log.info(registration.getName(), " Campaign Ends today ", date);
             registration.setStatus((short) percentage);
             return sendAlert(registration,null, AlertType.COMPLETED, percentage);
         }
         return Mono.just(registration);

    }

    private Mono<CampaignRegistration> sendToStakeholders(CampaignRegistration registration,
                                                         AlertType alertType,
                                                         long completed) {
        return applicationRepository
                .findByStatusAndCampaignPublicId(ApplicationStatus.PENDING, registration.getPublicId())
                .flatMap(application -> sendAlert(registration, application.getSubmittedBy(), alertType, completed))
                .collectList()
                .map(campaignRegistrations -> sendAlert(registration, null, alertType, completed))
                .map(aBoolean -> registration);
    }

    private Mono<CampaignRegistration> sendAlert(CampaignRegistration registration,
                                                 String userId,
                                                 AlertType alertType,
                                                 long status) {
        return alertEvent.registerAlert(AlertRecord.builder()
                        .userPublicId((Objects.isNull(userId))? registration.getCreatedBy() : userId)
                        .title(alertType.getTitle(alertType.name(), registration.getName()))
                        .message(alertType.getMessage(alertType.getType(), registration.getName(), status))
                        .type(AlertType.CAMPAIGN.name())
                        .typeStatus("")
                        .status("NEW")
                        .build())
                .map(aBoolean -> registration);
    }

}
