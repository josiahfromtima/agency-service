package com.tima.platform.service;

import com.tima.platform.converter.BookmarkConverter;
import com.tima.platform.domain.Bookmark;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.response.BookmarkRecord;
import com.tima.platform.repository.BookmarkRepository;
import com.tima.platform.util.AppError;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import com.tima.platform.util.ReportSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND_INFLUENCER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/1/24
 */
@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final LoggerHelper log = LoggerHelper.newInstance(BookmarkService.class.getName());
    private final BookmarkRepository bookmarkRepository;

    private static final String BOOKMARK_MSG = "Bookmark request executed successfully";
    private static final String INVALID_BOOKMARK = "The Bookmark with that title is invalid";

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> bookmarkCampaign(String publicId, BookmarkRecord bookmarkRecord) {
        log.info("Adding new Campaign Bookmark Record...");
        Bookmark newBookmark = BookmarkConverter.mapToEntity(bookmarkRecord);
        newBookmark.setUserPublicId(publicId);
        return bookmarkRepository.save(newBookmark)
                .map(BookmarkConverter::mapToRecord)
                .map(indRecord -> AppUtil.buildAppResponse(indRecord, BOOKMARK_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.message(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getBookmarks(String publicId, ReportSettings settings) {
        log.info("Getting Campaign Bookmarks ", publicId);
        return bookmarkRepository.findByUserPublicId(publicId, setPage(settings))
                .collectList()
                .map(BookmarkConverter::mapToRecords)
                .map(indRecord -> AppUtil.buildAppResponse(indRecord, BOOKMARK_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getOneBookmark(String title) {
        log.info("GettingCampaign Bookmark Record  ", title);
        return getBookmark(title)
                .map(BookmarkConverter::mapToRecord)
                .map(indRecord -> AppUtil.buildAppResponse(indRecord, BOOKMARK_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> deleteBookmark(String title) {
        log.info("Delete  Campaign Bookmark Record  ", title);
        return getBookmark(title)
                .flatMap(bookmarkRepository::delete)
                .then(Mono.fromCallable(() -> AppUtil.buildAppResponse(title + " Deleted", BOOKMARK_MSG)));
    }

    private Mono<Bookmark> getBookmark(String title) {
        return bookmarkRepository.findByTitle(title)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_BOOKMARK), BAD_REQUEST.value()));
    }

    private Pageable setPage(ReportSettings settings) {
        return PageRequest.of(settings.getPage(), settings.getSize(),
                Sort.Direction.fromString(settings.getSortIn()), settings.getSortBy());
    }
}
