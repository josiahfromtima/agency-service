package com.tima.platform.converter;

import com.tima.platform.domain.InfluencerApplication;
import com.tima.platform.model.api.request.InfluencerRecord;
import com.tima.platform.model.api.response.InfluencerApplicationRecord;
import com.tima.platform.model.constant.ApplicationStatus;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/20/23
 */
public class InfluencerApplicationConverter {
    private InfluencerApplicationConverter() {}

    public static synchronized InfluencerApplication mapToEntity(InfluencerApplicationRecord dto) {
        return InfluencerApplication.builder()
                .applicationId(dto.applicationId())
                .campaignPublicId(dto.campaignPublicId())
                .campaignName(dto.campaignName())
                .collaboration(getOrDefault(dto.collaboration(), ""))
                .userExperience(getOrDefault(dto.userExperience(), ""))
                .userExperienceBrief(getOrDefault(dto.userExperienceBrief(), ""))
                .userMotivationBrief(getOrDefault(dto.userMotivationBrief(), ""))
                .status(getOrDefault(dto.status(), ApplicationStatus.PENDING))
                .submittedBy(dto.submittedBy())
                .approvedBy(dto.approvedBy())
                .build();
    }

    public static synchronized InfluencerApplicationRecord mapToRecord(InfluencerApplication entity) {
        return  InfluencerApplicationRecord.builder()
                .applicationId(entity.getApplicationId())
                .applicationDate(entity.getApplicationDate())
                .approvedBy(entity.getApprovedBy())
                .campaignPublicId(entity.getCampaignPublicId())
                .campaignName(entity.getCampaignName())
                .campaignBudget(entity.getCampaignBudget())
                .campaignDescription(entity.getCampaignDescription())
                .username(entity.getUsername())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .profilePicture(entity.getProfilePicture())
                .socialMediaPlatform(entity.getSocialMediaPlatform())
                .collaboration(entity.getCollaboration())
                .userExperience(entity.getUserExperience())
                .userExperienceBrief(entity.getUserExperienceBrief())
                .userMotivationBrief(entity.getUserMotivationBrief())
                .status(entity.getStatus())
                .submittedBy(entity.getSubmittedBy())
                .reviewedBy(entity.getReviewedBy())
                .campaignLogo(entity.getCampaignLogo())
                .createdOn(entity.getCreatedOn())
                .editedOn(entity.getEditedOn())
                .build();
    }

    public static synchronized InfluencerRecord mapToInfluencer(InfluencerApplication entity) {
        return InfluencerRecord.builder()
                .publicId(entity.getSubmittedBy())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .phoneNumber(entity.getPhoneNumber())
                .profilePicture(entity.getProfilePicture())
                .build();
    }

    public static synchronized List<InfluencerApplicationRecord> mapToRecords(List<InfluencerApplication> entities) {
        return entities
                .stream()
                .map(InfluencerApplicationConverter::mapToRecord)
                .toList();
    }

    public static synchronized List<InfluencerApplication> mapToEntities(List<InfluencerApplicationRecord> records) {
        return records
                .stream()
                .map(InfluencerApplicationConverter::mapToEntity)
                .toList();
    }
    public static synchronized List<InfluencerRecord> mapToInfluencers(List<InfluencerApplication> entities) {
        return entities
                .stream()
                .map(InfluencerApplicationConverter::mapToInfluencer)
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
