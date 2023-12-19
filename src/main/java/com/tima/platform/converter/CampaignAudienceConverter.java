package com.tima.platform.converter;

import com.google.gson.reflect.TypeToken;
import com.tima.platform.domain.CampaignAudience;
import com.tima.platform.model.api.response.CampaignAudienceRecord;
import com.tima.platform.util.AppUtil;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
public class CampaignAudienceConverter {
    private CampaignAudienceConverter() {}

    public static synchronized CampaignAudience mapToEntity(CampaignAudienceRecord dto) {
        return CampaignAudience.builder()
                .size(AppUtil.gsonInstance().toJson(dto.size()))
                .gender(AppUtil.gsonInstance().toJson(dto.gender()))
                .ageGroup(AppUtil.gsonInstance().toJson(dto.ageGroup()))
                .location(AppUtil.gsonInstance().toJson(dto.location()))
                .monthlyIncome(AppUtil.gsonInstance().toJson(dto.monthlyIncome()))
                .build();
    }

    public static synchronized CampaignAudienceRecord mapToRecord(CampaignAudience entity) {
        return  CampaignAudienceRecord.builder()
                .size(json(entity.getSize()))
                .gender(json(entity.getGender()))
                .ageGroup(json(entity.getAgeGroup()))
                .location(json(entity.getLocation()))
                .monthlyIncome(json(entity.getMonthlyIncome()))
                .build();
    }

    public static synchronized List<CampaignAudienceRecord> mapToRecords(List<CampaignAudience> entities) {
        return entities
                .stream()
                .map(CampaignAudienceConverter::mapToRecord)
                .toList();
    }
    public static synchronized List<CampaignAudience> mapToEntities(List<CampaignAudienceRecord> records) {
        return records
                .stream()
                .map(CampaignAudienceConverter::mapToEntity)
                .toList();
    }
    private static List<String> json(String value) {
        return AppUtil.gsonInstance().fromJson(value, new TypeToken<List<String>>(){}.getType());
    }
}
