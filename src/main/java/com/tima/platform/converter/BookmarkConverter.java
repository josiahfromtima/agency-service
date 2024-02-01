package com.tima.platform.converter;

import com.tima.platform.domain.Bookmark;
import com.tima.platform.model.api.response.BookmarkRecord;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/1/24
 */
public class BookmarkConverter {
    private BookmarkConverter() {}

    public static synchronized Bookmark mapToEntity(BookmarkRecord dto) {
        return Bookmark.builder()
                .title(getOrDefault(dto.title(), ""))
                .campaignPublicId(getOrDefault(dto.campaignPublicId(), ""))
                .build();
    }

    public static synchronized BookmarkRecord mapToRecord(Bookmark entity) {
        return  BookmarkRecord.builder()
                .title(entity.getTitle())
                .campaignPublicId(entity.getCampaignPublicId())
                .createdOn(entity.getCreatedOn())
                .build();
    }

    public static synchronized List<BookmarkRecord> mapToRecords(List<Bookmark> entities) {
        return entities
                .stream()
                .map(BookmarkConverter::mapToRecord)
                .toList();
    }
    public static synchronized List<Bookmark> mapToEntities(List<BookmarkRecord> records) {
        return records
                .stream()
                .map(BookmarkConverter::mapToEntity)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getOrDefault(Object value, T t){
        if( Objects.isNull(value) ) {
            return t;
        }
        return (T) value;
    }
}
