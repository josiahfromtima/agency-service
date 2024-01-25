package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tima.platform.model.api.request.CampaignCreativeRecord;
import com.tima.platform.model.api.request.CampaignInfluencerRecord;
import com.tima.platform.model.api.request.CampaignOverviewRecord;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record CampaignRegistrationRecord(String publicId,
                                         @NotBlank(message = "The Brand Name is Required")
                                         String brandName,
                                         @NotNull(message = "Overview Segment is Required")
                                         CampaignOverviewRecord overview,
                                         @NotNull(message = "Influencer Segment is Required")
                                         CampaignInfluencerRecord influencer,
                                         @NotNull(message = "Creative Segment is Required")
                                         CampaignCreativeRecord creative,
                                         Short status, String createdBy, Instant createdOn) {}
