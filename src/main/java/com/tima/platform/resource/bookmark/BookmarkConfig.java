package com.tima.platform.resource.bookmark;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/12/23
 */
@Configuration
public class BookmarkConfig {
    public static final String API_V1_URL = "/v1";

    private static final String TITLE = "/title/{title}";
    public static final String BOOKMARK_BASE = API_V1_URL + "/bookmarks";
    public static final String BOOKMARK_PROFILE_BASE = BOOKMARK_BASE + "/influencer";
    public static final String ADD_BOOKMARK = BOOKMARK_BASE;
    public static final String GET_BOOKMARKS = BOOKMARK_BASE;
    public static final String GET_BOOKMARK = BOOKMARK_BASE + TITLE;
    public static final String DELETE_BOOKMARK = BOOKMARK_BASE + TITLE;
    public static final String ADD_PROFILE_BOOKMARK = BOOKMARK_PROFILE_BASE;
    public static final String GET_PROFILE_BOOKMARKS = BOOKMARK_PROFILE_BASE;
    public static final String GET_PROFILE_BOOKMARK = BOOKMARK_PROFILE_BASE + TITLE;
    public static final String DELETE_PROFILE_BOOKMARK = BOOKMARK_PROFILE_BASE + TITLE;

    @Bean
    public RouterFunction<ServerResponse> bookmarkEndpointHandler(BookmarkHandler handler) {
        return route()
                .GET(GET_BOOKMARKS, accept(MediaType.APPLICATION_JSON), handler::getUserBookmarks)
                .GET(GET_BOOKMARK, accept(MediaType.APPLICATION_JSON), handler::getBookmark)
                .POST(ADD_BOOKMARK, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::addNewBookmark)
                .DELETE(DELETE_BOOKMARK, accept(MediaType.APPLICATION_JSON), handler::deleteBookmark)
                .GET(GET_PROFILE_BOOKMARKS, accept(MediaType.APPLICATION_JSON), handler::getProfileUserBookmarks)
                .GET(GET_PROFILE_BOOKMARK, accept(MediaType.APPLICATION_JSON), handler::getProfileBookmark)
                .POST(ADD_PROFILE_BOOKMARK, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::addNewProfileBookmark)
                .DELETE(DELETE_PROFILE_BOOKMARK, accept(MediaType.APPLICATION_JSON), handler::deleteProfileBookmark)
                .build();
    }
}
