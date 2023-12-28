package com.tima.platform.converter;

import com.google.gson.reflect.TypeToken;
import com.tima.platform.domain.ClientIndustry;
import com.tima.platform.model.api.response.ClientIndustryRecord;
import com.tima.platform.util.AppUtil;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/26/23
 */
public class ClientIndustryConverter {
    private ClientIndustryConverter() {}

    public static synchronized ClientIndustry mapToEntity(ClientIndustryRecord dto) {
        return ClientIndustry.builder()
                .userPublicId(dto.userPublicId())
                .selectedIndustries(AppUtil.gsonInstance().toJson(getOrDefault(dto.selectedIndustries(), "[]")))
                .build();
    }

    public static synchronized ClientIndustryRecord mapToRecord(ClientIndustry entity) {
        return  ClientIndustryRecord.builder()
                .userPublicId(entity.getUserPublicId())
                .selectedIndustries(json(entity.getSelectedIndustries()))
                .build();
    }

    public static synchronized List<ClientIndustryRecord> mapToRecords(List<ClientIndustry> entities) {
        return entities
                .stream()
                .map(ClientIndustryConverter::mapToRecord)
                .toList();
    }
    public static synchronized List<ClientIndustry> mapToEntities(List<ClientIndustryRecord> records) {
        return records
                .stream()
                .map(ClientIndustryConverter::mapToEntity)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getOrDefault(Object value, T t){
        if( Objects.isNull(value) ) {
            return t;
        }
        return (T) value;
    }

    private static List<String> json(String value) {
        return AppUtil.gsonInstance().fromJson(value, new TypeToken<List<String>>(){}.getType());
    }
}
