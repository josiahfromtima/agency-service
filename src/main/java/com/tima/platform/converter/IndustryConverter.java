package com.tima.platform.converter;

import com.tima.platform.domain.Industry;
import com.tima.platform.model.api.response.IndustryRecord;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/12/23
 */
public class IndustryConverter {
    private IndustryConverter() {}

    public static synchronized Industry mapToEntity(IndustryRecord dto) {
        return Industry.builder()
                .name(dto.name())
                .description(getOrDefault(dto.description(), ""))
                .build();
    }

    public static synchronized IndustryRecord mapToRecord(Industry entity) {
        return  IndustryRecord.builder()
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }

    public static synchronized List<IndustryRecord> mapToRecords(List<Industry> entities) {
        return entities
                .stream()
                .map(IndustryConverter::mapToRecord)
                .toList();
    }
    public static synchronized List<Industry> mapToEntities(List<IndustryRecord> records) {
        return records
                .stream()
                .map(IndustryConverter::mapToEntity)
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
