package com.tima.platform.domain;

import com.tima.platform.model.constant.ApplicationStatus;
import com.tima.platform.util.AppUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/20/23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("public.influencer_application")
public class InfluencerApplication implements Serializable, Persistable<Integer> {

    @Id
    private Integer id;
    private Integer campaignId;
    private String campaignPublicId;
    private String campaignName;
    private BigDecimal campaignBudget;
    private String campaignDescription;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String profilePicture;
    private String socialMediaPlatform;
    private String collaboration;
    private String userExperience;
    private String userExperienceBrief;
    private String userMotivationBrief;
    private ApplicationStatus status;
    private LocalDate applicationDate;
    private String approvedBy;
    private String submittedBy;
    private String applicationId;
    private String reviewedBy;
    private Instant createdOn;
    private Instant editedOn;

    @Override
    public boolean isNew() {
        boolean newRecord = AppUtil.isNewRecord(id);
        if(newRecord) {
            createdOn = Instant.now();
            applicationDate = LocalDate.now();
            applicationId = UUID.randomUUID().toString();
        }
        editedOn = Instant.now();
        return newRecord;
    }
}
