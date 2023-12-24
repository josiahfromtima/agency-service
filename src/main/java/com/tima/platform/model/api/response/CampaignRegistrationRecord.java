package com.tima.platform.model.api.response;

import com.tima.platform.model.api.request.CampaignCreativeRecord;
import com.tima.platform.model.api.request.CampaignInfluencerRecord;
import com.tima.platform.model.api.request.CampaignOverviewRecord;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Builder
public record CampaignRegistrationRecord(String publicId, CampaignOverviewRecord overview,
                                         CampaignInfluencerRecord influencer,
                                         CampaignCreativeRecord creative) {}
