package com.tima.platform.service;

import com.tima.platform.domain.CampaignRegistration;
import com.tima.platform.repository.CampaignRegistrationRepository;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

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

    @Scheduled(cron = "${app.scheduler.campaign.status}")
    public void runCampaignStatus() {
        log.info("Running Campaign Status Update Job....");
        getValidCampaigns()
                .map(this::getDateDifference)
                .collectList()
                .map(registrationRepository::saveAll)
                .flatMap(Flux::collectList)
                .subscribe(log::info);
    }

    private Flux<CampaignRegistration> getValidCampaigns() {
        return registrationRepository.findByEndDateAfterOrEndDate(LocalDate.now(), LocalDate.now());
    }

    private CampaignRegistration getDateDifference(CampaignRegistration registration) {
        LocalDate today = LocalDate.now();
        long noOfDays = registration.getEndDate().toEpochDay() - registration.getStartDate().toEpochDay();
        long daysFromToday = today.toEpochDay() - registration.getStartDate().toEpochDay();
        if(registration.getStartDate().isAfter(today)) registration.setStatus((short) 0);
        else registration.setStatus((short) ( ((double) daysFromToday/ noOfDays) * 100));
        return registration;
    }

}
