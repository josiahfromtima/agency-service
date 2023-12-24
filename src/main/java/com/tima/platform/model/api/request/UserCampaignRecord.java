package com.tima.platform.model.api.request;

import com.tima.platform.domain.CampaignRegistration;
import com.tima.platform.model.api.response.FullUserProfileRecord;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/22/23
 */
@Builder
public record UserCampaignRecord(CampaignRegistration campaign, FullUserProfileRecord userProfileRecord) {}
