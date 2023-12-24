package com.tima.platform.domain;

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
 * @Date: 12/19/23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("public.campaign_registration")
public class CampaignRegistration implements Serializable, Persistable<Integer> {

    @Id
    private Integer id;
    private String name;
    private String briefDescription;
    private String website;
    private BigDecimal plannedBudget;
    private BigDecimal costPerPost;
    private String socialMediaPlatforms;
    private String influencerCategory;
    private String audienceSize;
    private String audienceGender;
    private String audienceAgeGroup;
    private String audienceLocation;
    private String paymentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String contentType;
    private String contentPlacement;
    private String creativeBrief;
    private String rules;
    private String creativeTone;
    private String referenceLink;
    private String awarenessObjective;
    private String acquisitionObjective;
    private String thumbnail;
    private Boolean visibility;
    private String publicId;
    private String status;
    private Instant createdOn;
    private Instant editedOn;

    @Override
    public boolean isNew() {
        boolean newRecord = AppUtil.isNewRecord(id);
        if(newRecord) {
            createdOn = Instant.now();
            publicId = UUID.randomUUID().toString();
        }
        editedOn = Instant.now();
        return newRecord;
    }
}
