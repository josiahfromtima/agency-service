package com.tima.platform.resource.bookmark;

import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.config.CustomValidator;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.model.api.response.BookmarkRecord;
import com.tima.platform.service.BookmarkService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.tima.platform.model.api.ApiResponse.buildServerResponse;
import static com.tima.platform.model.api.ApiResponse.reportSettings;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/12/23
 */
@Service
@RequiredArgsConstructor
public class BookmarkHandler {
    LoggerHelper log = LoggerHelper.newInstance(BookmarkHandler.class.getName());

    private final BookmarkService bookmarkService;
    private final CustomValidator validator;

    private static final String X_FORWARD_FOR = "X-Forwarded-For";
    /**
     *  This section marks the industries activities
     */
    public Mono<ServerResponse> getUserBookmarks(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Get Bookmarks Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(id -> bookmarkService.getBookmarks(id, reportSettings(request)))
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> getBookmark(ServerRequest request)  {
        String title = request.pathVariable("title");
        log.info("Get Bookmark Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(bookmarkService.getOneBookmark(title));
    }

    public Mono<ServerResponse> addNewBookmark(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        Mono<BookmarkRecord> recordMono = request.bodyToMono(BookmarkRecord.class)
                .doOnNext(validator::validateEntries);
        log.info("Added a new bookmark Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .flatMap(id -> recordMono
                        .map(bookmarkRecord ->  bookmarkService.bookmarkCampaign(id, bookmarkRecord)))
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> deleteBookmark(ServerRequest request)  {
        String title = request.pathVariable("title");
        log.info("Delete Bookmark Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(bookmarkService.deleteBookmark(title));
    }

}
