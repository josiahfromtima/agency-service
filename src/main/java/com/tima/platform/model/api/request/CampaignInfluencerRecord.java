package com.tima.platform.model.api.request;

import lombok.Builder;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Builder
public record CampaignInfluencerRecord(List<String> influencerCategory, List<String> audienceSize,
                                       List<String> audienceGender, List<String> audienceAgeGroup,
                                       List<String> audienceLocation) {}