package com.tima.platform.converter;

import com.google.gson.reflect.TypeToken;
import com.tima.platform.domain.CampaignCreative;
import com.tima.platform.model.api.response.CampaignCreativeRecord;
import com.tima.platform.util.AppUtil;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
public class CampaignCreativeConverter {
    private CampaignCreativeConverter() {}

    public static synchronized CampaignCreative mapToEntity(CampaignCreativeRecord dto) {
        return CampaignCreative.builder()
                .contentType(AppUtil.gsonInstance().toJson(dto.contentType()))
                .contentPlacement(AppUtil.gsonInstance().toJson(dto.contentPlacement()))
                .creativeTone(AppUtil.gsonInstance().toJson(dto.creativeTone()))
                .objectiveAwareness(AppUtil.gsonInstance().toJson(dto.objectiveAwareness()))
                .objectiveAcquisition(AppUtil.gsonInstance().toJson(dto.objectiveAcquisition()))
                .build();
    }

    public static synchronized CampaignCreativeRecord mapToRecord(CampaignCreative entity) {
        return  CampaignCreativeRecord.builder()
                .contentType(json(entity.getContentType()))
                .contentPlacement(json(entity.getContentPlacement()))
                .creativeTone(json(entity.getCreativeTone()))
                .objectiveAwareness(json(entity.getObjectiveAwareness()))
                .objectiveAcquisition(json(entity.getObjectiveAcquisition()))
                .build();
    }

    public static synchronized List<CampaignCreativeRecord> mapToRecords(List<CampaignCreative> entities) {
        return entities
                .stream()
                .map(CampaignCreativeConverter::mapToRecord)
                .toList();
    }
    public static synchronized List<CampaignCreative> mapToEntities(List<CampaignCreativeRecord> records) {
        return records
                .stream()
                .map(CampaignCreativeConverter::mapToEntity)
                .toList();
    }
    private static List<String> json(String value) {
        return AppUtil.gsonInstance().fromJson(value, new TypeToken<List<String>>(){}.getType());
    }
}
