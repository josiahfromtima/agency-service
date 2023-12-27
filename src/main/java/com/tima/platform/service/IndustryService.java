package com.tima.platform.service;

import com.tima.platform.converter.IndustryConverter;
import com.tima.platform.domain.Industry;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.IndustryUpdateRecord;
import com.tima.platform.model.api.response.IndustryRecord;
import com.tima.platform.repository.IndustryRepository;
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
 * @Date: 12/12/23
 */
@Service
@RequiredArgsConstructor
public class IndustryService {
    private final LoggerHelper log = LoggerHelper.newInstance(IndustryService.class.getName());
    private final IndustryRepository industryRepository;

    private static final String INDUSTRY_MSG = "Industry request executed successfully";
    private static final String INVALID_INDUSTRY = "The industry name is invalid";
    private static final String ERROR_MSG = "The industry record mutation could not be performed";


    public Mono<AppResponse> getIndustries() {
        log.info("Getting ALl Industry Records...");
        return industryRepository.findAll()
                .collectList()
                .map(IndustryConverter::mapToRecords)
                .map(countryRecords -> AppUtil.buildAppResponse(countryRecords, INDUSTRY_MSG));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> addIndustry(IndustryRecord industryRecord) {
        log.info("Adding new Industry Record...");
        return industryRepository.save(IndustryConverter.mapToEntity(industryRecord))
                .map(IndustryConverter::mapToRecord)
                .map(indRecord -> AppUtil.buildAppResponse(indRecord, INDUSTRY_MSG))
                .onErrorResume(throwable -> handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> updateIndustry(IndustryUpdateRecord industryRecord) {
        log.info("Updating the Industry Record...");
        return validateIndustry(industryRecord.oldName())
                .flatMap(industry -> {
                    industry.setName(industryRecord.newName());
                    industry.setDescription(industryRecord.description());
                    return industryRepository.save(industry);
                })
                .map(IndustryConverter::mapToRecord)
                .map(indRecord -> AppUtil.buildAppResponse(indRecord, INDUSTRY_MSG))
                .onErrorResume(throwable -> handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> deleteIndustry(String name) {
        log.info("Delete the Industry Record  ", name);
        return validateIndustry(name)
                .flatMap(industryRepository::delete)
                .then(Mono.fromCallable(() -> AppUtil.buildAppResponse(name + " Deleted", INDUSTRY_MSG)));
    }

    private Mono<Industry> validateIndustry(String name) {
        return industryRepository.findByName(name)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_INDUSTRY), BAD_REQUEST.value()));
    }
}
