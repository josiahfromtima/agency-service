package com.tima.platform.converter;

import com.tima.platform.domain.SocialMedia;
import com.tima.platform.model.api.response.SocialMediaRecord;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/25/24
 */
public class SocialMediaConverter {
    private SocialMediaConverter() {}

    public static synchronized SocialMedia mapToEntity(SocialMediaRecord dto) {
        return SocialMedia.builder()
                .name(dto.name())
                .accessToken(getOrDefault(dto.accessToken(), ""))
                .logo(getOrDefault(dto.logo(),"default.png"))
                .build();
    }

    public static synchronized SocialMediaRecord mapToRecord(SocialMedia entity) {
        return  SocialMediaRecord.builder()
                .name(entity.getName())
                .logo(entity.getLogo())
                .build();
    }

    public static synchronized SocialMediaRecord mapToFullRecord(SocialMedia entity) {
        return  SocialMediaRecord.builder()
                .name(entity.getName())
                .logo(entity.getLogo())
                .accessToken(entity.getAccessToken())
                .expiresIn(entity.getExpiresIn())
                .createdOn(entity.getCreatedOn())
                .expiresOn(entity.getExpiresOn())
                .build();
    }

    public static synchronized List<SocialMediaRecord> mapToRecords(List<SocialMedia> entities) {
        return entities
                .stream()
                .map(SocialMediaConverter::mapToRecord)
                .toList();
    }

    public static synchronized List<SocialMediaRecord> mapToFullRecords(List<SocialMedia> entities) {
        return entities
                .stream()
                .map(SocialMediaConverter::mapToFullRecord)
                .toList();
    }
    public static synchronized List<SocialMedia> mapToEntities(List<SocialMediaRecord> records) {
        return records
                .stream()
                .map(SocialMediaConverter::mapToEntity)
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
