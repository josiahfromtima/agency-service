package com.tima.platform.converter;

import com.tima.platform.domain.InfluencerCategory;
import com.tima.platform.model.api.response.InfluencerCategoryRecord;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/12/23
 */
public class InfluencerCategoryConverter {
    private InfluencerCategoryConverter() {}

    public static synchronized InfluencerCategory mapToEntity(InfluencerCategoryRecord dto) {
        return InfluencerCategory.builder()
                .name(dto.name())
                .description(getOrDefault(dto.description(), ""))
                .build();
    }

    public static synchronized InfluencerCategoryRecord mapToRecord(InfluencerCategory entity) {
        return  InfluencerCategoryRecord.builder()
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }

    public static synchronized List<InfluencerCategoryRecord> mapToRecords(List<InfluencerCategory> entities) {
        return entities
                .stream()
                .map(InfluencerCategoryConverter::mapToRecord)
                .toList();
    }
    public static synchronized List<InfluencerCategory> mapToEntities(List<InfluencerCategoryRecord> records) {
        return records
                .stream()
                .map(InfluencerCategoryConverter::mapToEntity)
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
