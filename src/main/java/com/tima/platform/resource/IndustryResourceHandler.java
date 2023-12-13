package com.tima.platform.resource;

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

    /**
     *  This section marks the industries activities
     */
    public Mono<ServerResponse> getIndustries(ServerRequest request)  {
        log.info("Get Registered Industries Requested", request.remoteAddress().orElse(null));

        try {
            return industryService.getIndustries()
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public Mono<ServerResponse> addNewIndustry(ServerRequest request)  {
        Mono<IndustryRecord> recordMono = request.bodyToMono(IndustryRecord.class);
        log.info("Registered a new industry Requested", request.remoteAddress().orElse(null));

        try {
            return recordMono.flatMap(industryService::addIndustry)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }
    public Mono<ServerResponse> updateIndustry(ServerRequest request)  {
        Mono<IndustryUpdateRecord> recordMono = request.bodyToMono(IndustryUpdateRecord.class);
        log.info("Update Industry Requested", request.remoteAddress().orElse(null));

        try {
            return recordMono.flatMap(industryService::updateIndustry)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }
    public Mono<ServerResponse> deleteIndustry(ServerRequest request)  {
        String name = request.pathVariable("name");
        log.info("Delete Industry Requested", request.remoteAddress().orElse(null));

        try {
            return industryService.deleteIndustry(name)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    /**
     *  This section marks the influencer categories activities
     */
    public Mono<ServerResponse> getInfluencerCategories(ServerRequest request)  {
        log.info("Get Registered Influeencer Industries Requested", request.remoteAddress().orElse(null));

        try {
            return categoryService.getIndustries()
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public Mono<ServerResponse> addNewInfluencerCategory(ServerRequest request)  {
        Mono<InfluencerCategoryRecord> recordMono = request.bodyToMono(InfluencerCategoryRecord.class);
        log.info("Registered a new industry Requested", request.remoteAddress().orElse(null));

        try {
            return recordMono.flatMap(categoryService::addIndustry)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }
    public Mono<ServerResponse> updateInfluencerCategory(ServerRequest request)  {
        Mono<IndustryUpdateRecord> recordMono = request.bodyToMono(IndustryUpdateRecord.class);
        log.info("Update Influencer Industry Requested", request.remoteAddress().orElse(null));

        try {
            return recordMono.flatMap(categoryService::updateInfluencerCategory)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }
    public Mono<ServerResponse> deleteInfluencerCategory(ServerRequest request)  {
        String name = request.pathVariable("name");
        log.info("Delete Influencer Industry Requested", request.remoteAddress().orElse(null));

        try {
            return categoryService.deleteIndustry(name)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

}
