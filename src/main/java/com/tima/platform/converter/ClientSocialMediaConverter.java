package com.tima.platform.converter;

import com.google.gson.reflect.TypeToken;
import com.tima.platform.domain.ClientSocialMedia;
import com.tima.platform.model.api.request.ClientSelectedSocialMedia;
import com.tima.platform.model.api.response.ClientSocialMediaRecord;
import com.tima.platform.util.AppUtil;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/25/24
 */
public class ClientSocialMediaConverter {
    private ClientSocialMediaConverter() {}

    public static synchronized ClientSocialMedia mapToEntity(ClientSocialMediaRecord dto) {
        return ClientSocialMedia.builder()
                .userId(dto.userId())
                .url(dto.url())
                .selectedSocialMedia(AppUtil.gsonInstance().toJson(getOrDefault(dto.selectedSocialMedia(), "[]")))
                .build();
    }

    public static synchronized ClientSocialMediaRecord mapToRecord(ClientSocialMedia entity) {
        return  ClientSocialMediaRecord.builder()
                .userId(entity.getUserId())
                .selectedSocialMedia(json(entity.getSelectedSocialMedia()))
                .url(entity.getUrl())
                .build();
    }

    public static synchronized List<ClientSocialMediaRecord> mapToRecords(List<ClientSocialMedia> entities) {
        return entities
                .stream()
                .map(ClientSocialMediaConverter::mapToRecord)
                .toList();
    }
    public static synchronized List<ClientSocialMedia> mapToEntities(List<ClientSocialMediaRecord> records) {
        return records
                .stream()
                .map(ClientSocialMediaConverter::mapToEntity)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getOrDefault(Object value, T t){
        if( Objects.isNull(value) ) {
            return t;
        }
        return (T) value;
    }

    private static <T> List<T> json(String value) {
        return AppUtil.gsonInstance().fromJson(value, new TypeToken<List<ClientSelectedSocialMedia>>(){}.getType());
    }

    public  static String json(Object data) {
        return AppUtil.gsonInstance().toJson(data);
    }
}
