package com.tima.platform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.reflect.TypeToken;
import com.tima.platform.converter.ClientIndustryConverter;
import com.tima.platform.domain.ClientIndustry;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.response.ClientIndustryRecord;
import com.tima.platform.repository.ClientIndustryRepository;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND_INFLUENCER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/26/23
 */
@Service
@RequiredArgsConstructor
public class ClientIndustryService {
    private final LoggerHelper log = LoggerHelper.newInstance(IndustryService.class.getName());
    private final ClientIndustryRepository clientIndustryRepository;

    private static final String CLIENT_MSG = "Client request executed successfully";
    private static final String INVALID_CLIENT = "The User Id is invalid";
    private static final String ERROR_MSG = "The client industry record mutation could not be performed";

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getIndustries(String publicId) {
        log.info("Getting Client industry Record...");
        return validateUser(publicId)
                .map(ClientIndustryConverter::mapToRecord)
                .map(clientRecord -> AppUtil.buildAppResponse(clientRecord, CLIENT_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> updateClientIndustry(JsonNode node, String publicId) {
        log.info("Updating the Client Industry Record... ", node);
        return validateUser(publicId)
                .flatMap(industry -> {
                    ClientIndustry modified = ClientIndustryConverter.mapToEntity(ClientIndustryRecord.builder()
                                    .userPublicId(publicId)
                                    .selectedIndustries(json(node.at("/selectedIndustries").toString()))
                            .build());
                    modified.setId(industry.getId());
                    modified.setCreatedOn(industry.getCreatedOn());
                    return clientIndustryRepository.save(modified);
                })
                .map(ClientIndustryConverter::mapToRecord)
                .map(indRecord -> AppUtil.buildAppResponse(indRecord, CLIENT_MSG))
                .onErrorResume(throwable -> handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> addClientIndustry(ClientIndustryRecord industryRecord) {
        log.info("Adding new Client and Industry Record...");
        if(!validateEntry(industryRecord))
            return handleOnErrorResume(new AppException(INVALID_CLIENT), BAD_REQUEST.value());
        return clientIndustryRepository.save(ClientIndustryConverter.mapToEntity(industryRecord))
                .map(ClientIndustryConverter::mapToRecord)
                .map(indRecord -> AppUtil.buildAppResponse(indRecord, CLIENT_MSG))
                .onErrorResume(throwable -> handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> deleteClientIndustry(String publicId) {
        log.info("Delete the Client Industry Record  ", publicId);
        return validateUser(publicId)
                .flatMap(clientIndustryRepository::delete)
                .then(Mono.fromCallable(() -> AppUtil.buildAppResponse(publicId + " Deleted", CLIENT_MSG)));
    }

    private Mono<ClientIndustry> validateUser(String publicId) {
        return clientIndustryRepository.findByUserPublicId(publicId)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_CLIENT), BAD_REQUEST.value()));
    }

    private static List<String> json(String value) {
        return AppUtil.gsonInstance().fromJson(value, new TypeToken<List<String>>(){}.getType());
    }

    public boolean validateEntry(ClientIndustryRecord industryRecord) {
        long count = industryRecord.userPublicId().codePoints().filter(ch -> ch == '-').count();
        if(count != 4) return false;
        else return !industryRecord.selectedIndustries().isEmpty();
    }
}
