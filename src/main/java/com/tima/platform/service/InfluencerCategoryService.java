package com.tima.platform.service;

import com.tima.platform.converter.InfluencerCategoryConverter;
import com.tima.platform.domain.InfluencerCategory;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.IndustryUpdateRecord;
import com.tima.platform.model.api.response.InfluencerCategoryRecord;
import com.tima.platform.repository.InfluencerCategoryRepository;
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
public class InfluencerCategoryService {
    private final LoggerHelper log = LoggerHelper.newInstance(InfluencerCategoryService.class.getName());
    private final InfluencerCategoryRepository categoryRepository;

    private static final String INFLUENCE_MSG = "Influencer Industry request executed successfully";
    private static final String INVALID_INFLUENCE = "The Influencer industry name is invalid";
    private static final String ERROR_MSG = "The industry record mutation could not be performed";

    public Mono<AppResponse> getIndustries() {
        log.info("Getting ALl Industry Records...");
        return categoryRepository.findAll()
                .collectList()
                .map(InfluencerCategoryConverter::mapToRecords)
                .map(countryRecords -> AppUtil.buildAppResponse(countryRecords, INFLUENCE_MSG));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> addIndustry(InfluencerCategoryRecord categoryRecord) {
        log.info("Adding new Influencer Industry Record...");
        return categoryRepository.save(InfluencerCategoryConverter.mapToEntity(categoryRecord))
                .map(InfluencerCategoryConverter::mapToRecord)
                .map(indRecord -> AppUtil.buildAppResponse(indRecord, INFLUENCE_MSG))
                .onErrorResume(throwable -> handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> updateInfluencerCategory(IndustryUpdateRecord industryRecord) {
        log.info("Updating the Influencer Industry Record...");
        return validateIndustry(industryRecord.oldName())
                .flatMap(industry -> {
                    industry.setName(industryRecord.newName());
                    industry.setDescription(industryRecord.description());
                    return categoryRepository.save(industry);
                })
                .map(InfluencerCategoryConverter::mapToRecord)
                .map(indRecord -> AppUtil.buildAppResponse(indRecord, INFLUENCE_MSG))
                .onErrorResume(throwable -> handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN)
    public Mono<AppResponse> deleteIndustry(String name) {
        log.info("Delete the Influencer Industry Record  ", name);
        return validateIndustry(name)
                .flatMap(categoryRepository::delete)
                .then(Mono.fromCallable(() -> AppUtil.buildAppResponse(name + " Deleted", INFLUENCE_MSG)));
    }

    private Mono<InfluencerCategory> validateIndustry(String name) {
        return categoryRepository.findByName(name)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_INFLUENCE), BAD_REQUEST.value()));
    }
}
