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
    public static final String BOOKMARK_BASE = API_V1_URL + "/bookmarks";
    public static final String ADD_BOOKMARK = BOOKMARK_BASE;
    public static final String GET_BOOKMARKS = BOOKMARK_BASE;
    public static final String GET_BOOKMARK = BOOKMARK_BASE + "/{title}";
    public static final String DELETE_BOOKMARK = BOOKMARK_BASE + "/{title}";

    @Bean
    public RouterFunction<ServerResponse> bookmarkEndpointHandler(BookmarkHandler handler) {
        return route()
                .GET(GET_BOOKMARKS, accept(MediaType.APPLICATION_JSON), handler::getUserBookmarks)
                .GET(GET_BOOKMARK, accept(MediaType.APPLICATION_JSON), handler::getBookmark)
                .POST(ADD_BOOKMARK, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::addNewBookmark)
                .DELETE(DELETE_BOOKMARK, accept(MediaType.APPLICATION_JSON), handler::deleteBookmark)
                .build();
    }
}
