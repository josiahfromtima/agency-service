package com.tima.platform.converter;

import com.google.gson.reflect.TypeToken;
import com.tima.platform.domain.CampaignRegistration;
import com.tima.platform.model.api.request.CampaignCreativeRecord;
import com.tima.platform.model.api.request.CampaignInfluencerRecord;
import com.tima.platform.model.api.request.CampaignOverviewRecord;
import com.tima.platform.model.api.response.CampaignRegistrationRecord;
import com.tima.platform.model.api.response.campaign.PastExperience;
import com.tima.platform.model.api.response.campaign.SearchResult;

import java.util.List;
import java.util.Objects;

import static com.tima.platform.util.AppUtil.gsonInstance;

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
                .brandName(dto.brandName())
                .name(sanitized.overview().name())
                .briefDescription(sanitized.overview().briefDescription())
                .website(dto.overview().website())
                .plannedBudget(sanitized.overview().plannedBudget())
                .costPerPost(sanitized.overview().costPerPost())
                .socialMediaPlatforms(gsonInstance().toJson(sanitized.overview().socialMediaPlatforms()))
                .influencerCategory(gsonInstance().toJson(sanitized.influencer().influencerCategory()))
                .audienceSize(gsonInstance().toJson(sanitized.influencer().audienceSize()))
                .audienceGender(gsonInstance().toJson(sanitized.influencer().audienceGender()))
                .audienceAgeGroup(gsonInstance().toJson(sanitized.influencer().audienceAgeGroup()))
                .audienceLocation(gsonInstance().toJson(sanitized.influencer().audienceLocation()))
                .paymentType(sanitized.creative().paymentType())
                .startDate(sanitized.creative().startDate())
                .endDate(sanitized.creative().endDate())
                .contentType(gsonInstance().toJson(sanitized.creative().contentType()))
                .contentPlacement(gsonInstance().toJson(sanitized.creative().contentPlacement()))
                .creativeBrief(sanitized.creative().creativeBrief())
                .creativeTone(gsonInstance().toJson(sanitized.creative().creativeTone()))
                .rules(sanitized.creative().rules())
                .referenceLink(sanitized.creative().referenceLink())
                .awarenessObjective(gsonInstance().toJson(sanitized.creative().awarenessObjective()))
                .acquisitionObjective(gsonInstance().toJson(sanitized.creative().acquisitionObjective()))
                .thumbnail(sanitized.creative().thumbnail())
                .visibility(sanitized.creative().visibility())
                .build();
    }

    public static synchronized CampaignRegistrationRecord mapToRecord(CampaignRegistration entity) {
        return  CampaignRegistrationRecord.builder()
                .publicId(entity.getPublicId())
                .brandName(entity.getBrandName())
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
                        .contentType(json(entity.getContentType()))
                        .contentPlacement(json(entity.getContentPlacement()))
                        .creativeTone(json(entity.getCreativeTone()))
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

    public static synchronized PastExperience mapToExperienceRecord(CampaignRegistration entity) {
        return PastExperience.builder()
                .campaignId(entity.getPublicId())
                .campaignBanner(entity.getThumbnail())
                .campaignName(String.format("%s %s", entity.getBrandName(), entity.getName()))
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .build();
    }

    public static synchronized SearchResult mapToSearchRecord(CampaignRegistration entity) {
        return SearchResult.builder()
                .campaignId(entity.getPublicId())
                .name(entity.getName())
                .description(entity.getBriefDescription())
                .banner(entity.getThumbnail())
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

    public static synchronized List<PastExperience> mapToExperienceRecords(List<CampaignRegistration> entities) {
        return entities
                .stream()
                .map(CampaignRegistrationConverter::mapToExperienceRecord)
                .toList();
    }

    public static synchronized List<SearchResult> mapToSearchRecords(List<CampaignRegistration> entities) {
        return entities
                .stream()
                .map(CampaignRegistrationConverter::mapToSearchRecord)
                .toList();
    }
    private static List<String> json(String value) {
        return gsonInstance().fromJson(value, new TypeToken<List<String>>(){}.getType());
    }

    private static CampaignRegistrationRecord validatePart(CampaignRegistrationRecord registration) {
        return CampaignRegistrationRecord
                .builder()
                .overview(Objects.isNull(registration.overview()) ?
                        CampaignOverviewRecord.builder().build() : registration.overview())
                .influencer(Objects.isNull(registration.influencer()) ?
                        CampaignInfluencerRecord.builder().build() : registration.influencer())
                .creative(Objects.isNull(registration.creative()) ?
                        CampaignCreativeRecord.builder().build() : registration.creative())
                .build();
    }
}
