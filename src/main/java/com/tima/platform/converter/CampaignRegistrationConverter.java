package com.tima.platform.converter;

import com.google.gson.reflect.TypeToken;
import com.tima.platform.domain.CampaignRegistration;
import com.tima.platform.model.api.request.CampaignCreativeRecord;
import com.tima.platform.model.api.request.CampaignInfluencerRecord;
import com.tima.platform.model.api.request.CampaignOverviewRecord;
import com.tima.platform.model.api.response.CampaignRegistrationRecord;
import com.tima.platform.util.AppUtil;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
public class CampaignRegistrationConverter {
    private CampaignRegistrationConverter() {}

    public static synchronized CampaignRegistration mapToEntity(CampaignRegistrationRecord dto) {
        CampaignRegistrationRecord sanitized = validatePart(dto);
        return CampaignRegistration.builder()
                .publicId(dto.publicId())
                .name(sanitized.overview().name())
                .briefDescription(sanitized.overview().briefDescription())
                .website(dto.overview().website())
                .plannedBudget(sanitized.overview().plannedBudget())
                .costPerPost(sanitized.overview().costPerPost())
                .socialMediaPlatforms(AppUtil.gsonInstance().toJson(sanitized.overview().socialMediaPlatforms()))
                .influencerCategory(AppUtil.gsonInstance().toJson(sanitized.influencer().influencerCategory()))
                .audienceSize(AppUtil.gsonInstance().toJson(sanitized.influencer().audienceSize()))
                .audienceGender(AppUtil.gsonInstance().toJson(sanitized.influencer().audienceGender()))
                .audienceAgeGroup(AppUtil.gsonInstance().toJson(sanitized.influencer().audienceAgeGroup()))
                .audienceLocation(AppUtil.gsonInstance().toJson(sanitized.influencer().audienceLocation()))
                .paymentType(sanitized.creative().paymentType())
                .startDate(sanitized.creative().startDate())
                .endDate(sanitized.creative().endDate())
                .contentType(sanitized.creative().contentType())
                .contentPlacement(sanitized.creative().contentPlacement())
                .creativeBrief(sanitized.creative().creativeBrief())
                .creativeTone(sanitized.creative().creativeTone())
                .rules(sanitized.creative().rules())
                .referenceLink(sanitized.creative().referenceLink())
                .awarenessObjective(AppUtil.gsonInstance().toJson(sanitized.creative().awarenessObjective()))
                .acquisitionObjective(AppUtil.gsonInstance().toJson(sanitized.creative().acquisitionObjective()))
                .thumbnail(sanitized.creative().thumbnail())
                .visibility(sanitized.creative().visibility())
                .build();
    }

    public static synchronized CampaignRegistrationRecord mapToRecord(CampaignRegistration entity) {
        return  CampaignRegistrationRecord.builder()
                .publicId(entity.getPublicId())
                .status(entity.getStatus())
                .overview(CampaignOverviewRecord.builder()
                        .name(entity.getName())
                        .briefDescription(entity.getBriefDescription())
                        .website(entity.getWebsite())
                        .plannedBudget(entity.getPlannedBudget())
                        .costPerPost(entity.getCostPerPost())
                        .socialMediaPlatforms(json(entity.getSocialMediaPlatforms()))
                        .build())
                .influencer(CampaignInfluencerRecord.builder()
                        .influencerCategory(json(entity.getInfluencerCategory()))
                        .audienceSize(json(entity.getAudienceSize()))
                        .audienceGender(json(entity.getAudienceGender()))
                        .audienceAgeGroup(json(entity.getAudienceAgeGroup()))
                        .audienceLocation(json(entity.getAudienceLocation()))
                        .build())
                .creative(CampaignCreativeRecord.builder()
                        .paymentType(entity.getPaymentType())
                        .creativeBrief(entity.getCreativeBrief())
                        .startDate(entity.getStartDate())
                        .endDate(entity.getEndDate())
                        .contentType(entity.getContentType())
                        .contentPlacement(entity.getContentPlacement())
                        .creativeTone(entity.getCreativeTone())
                        .referenceLink(entity.getReferenceLink())
                        .rules(entity.getRules())
                        .awarenessObjective(json(entity.getAwarenessObjective()))
                        .acquisitionObjective(json(entity.getAcquisitionObjective()))
                        .thumbnail(entity.getThumbnail())
                        .visibility(entity.getVisibility())
                        .build())
                .createdBy(entity.getCreatedBy())
                .createdOn(entity.getCreatedOn())
                .build();
    }

    public static synchronized List<CampaignRegistrationRecord> mapToRecords(List<CampaignRegistration> entities) {
        return entities
                .stream()
                .map(CampaignRegistrationConverter::mapToRecord)
                .toList();
    }
    public static synchronized List<CampaignRegistration> mapToEntities(List<CampaignRegistrationRecord> records) {
        return records
                .stream()
                .map(CampaignRegistrationConverter::mapToEntity)
                .toList();
    }
    private static List<String> json(String value) {
        return AppUtil.gsonInstance().fromJson(value, new TypeToken<List<String>>(){}.getType());
    }

    private static CampaignRegistrationRecord validatePart(CampaignRegistrationRecord record) {
        return CampaignRegistrationRecord
                .builder()
                .overview(Objects.isNull(record.overview()) ?
                        CampaignOverviewRecord.builder().build() : record.overview())
                .influencer(Objects.isNull(record.influencer()) ?
                        CampaignInfluencerRecord.builder().build() : record.influencer())
                .creative(Objects.isNull(record.creative()) ?
                        CampaignCreativeRecord.builder().build() : record.creative())
                .build();
    }
}
