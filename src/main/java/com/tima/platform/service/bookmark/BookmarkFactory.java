package com.tima.platform.service.bookmark;

import com.tima.platform.model.api.response.BookmarkRecord;
import com.tima.platform.util.ReportSettings;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/11/24
 */
public interface BookmarkFactory<T> {
    Mono<T> addBookmark(String publicId, BookmarkRecord bookmarkRecord);
    Mono<T> getBookmarks(String publicId, ReportSettings settings);
    Mono<T> getBookmarks(String token, String publicId, ReportSettings settings);
    Mono<T> getOneBookmark(String title);
    Mono<T> deleteBookmark(String title);
}
