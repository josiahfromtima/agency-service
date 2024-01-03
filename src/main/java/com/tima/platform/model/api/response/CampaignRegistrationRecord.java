package com.tima.platform.model.api.response;

import com.tima.platform.model.api.request.CampaignCreativeRecord;
import com.tima.platform.model.api.request.CampaignInfluencerRecord;
import com.tima.platform.model.api.request.CampaignOverviewRecord;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Builder
public record CampaignRegistrationRecord(String publicId,
                                         @NotNull(message = "Overview Segment is Required")
                                         CampaignOverviewRecord overview,
                                         @NotNull(message = "Influencer Segment is Required")
                                         CampaignInfluencerRecord influencer,
                                         @NotNull(message = "Creative Segment is Required")
                                         CampaignCreativeRecord creative,
                                         Short status, Instant createdOn) {}
