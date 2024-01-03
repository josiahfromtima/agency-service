package com.tima.platform.resource.industry;

import com.tima.platform.config.CustomValidator;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.model.api.request.IndustryUpdateRecord;
import com.tima.platform.model.api.response.IndustryRecord;
import com.tima.platform.model.api.response.InfluencerCategoryRecord;
import com.tima.platform.service.IndustryService;
import com.tima.platform.service.InfluencerCategoryService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.tima.platform.model.api.ApiResponse.buildServerResponse;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/12/23
 */
@Service
@RequiredArgsConstructor
public class IndustryResourceHandler {
    LoggerHelper log = LoggerHelper.newInstance(IndustryResourceHandler.class.getName());

    private final IndustryService industryService;
    private final InfluencerCategoryService categoryService;
    private final CustomValidator validator;

    /**
     *  This section marks the industries activities
     */
    public Mono<ServerResponse> getIndustries(ServerRequest request)  {
        log.info("Get Registered Industries Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(industryService.getIndustries());
    }

    public Mono<ServerResponse> addNewIndustry(ServerRequest request)  {
        Mono<IndustryRecord> recordMono = request.bodyToMono(IndustryRecord.class).doOnNext(validator::validateEntries);
        log.info("Registered a new industry Requested", request.remoteAddress().orElse(null));
        return recordMono
                .map(industryService::addIndustry)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> updateIndustry(ServerRequest request)  {
        Mono<IndustryUpdateRecord> recordMono = request.bodyToMono(IndustryUpdateRecord.class)
                .doOnNext(validator::validateEntries);
        log.info("Update Industry Requested", request.remoteAddress().orElse(null));
        return recordMono
                .map(industryService::updateIndustry)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> deleteIndustry(ServerRequest request)  {
        String name = request.pathVariable("name");
        log.info("Delete Industry Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(industryService.deleteIndustry(name));
    }

    /**
     *  This section marks the influencer categories activities
     */
    public Mono<ServerResponse> getInfluencerCategories(ServerRequest request)  {
        log.info("Get Registered Influeencer Industries Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(categoryService.getIndustries());
    }

    public Mono<ServerResponse> addNewInfluencerCategory(ServerRequest request)  {
        Mono<InfluencerCategoryRecord> recordMono = request.bodyToMono(InfluencerCategoryRecord.class)
                .doOnNext(validator::validateEntries);
        log.info("Registered a new industry Requested", request.remoteAddress().orElse(null));
        return recordMono
                .map(categoryService::addIndustry)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> updateInfluencerCategory(ServerRequest request)  {
        Mono<IndustryUpdateRecord> recordMono = request.bodyToMono(IndustryUpdateRecord.class)
                        .doOnNext(validator::validateEntries);
        log.info("Update Influencer Industry Requested", request.remoteAddress().orElse(null));
        return recordMono
                .map(categoryService::updateInfluencerCategory)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> deleteInfluencerCategory(ServerRequest request)  {
        String name = request.pathVariable("name");
        log.info("Delete Influencer Industry Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(categoryService.deleteIndustry(name));
    }

}
