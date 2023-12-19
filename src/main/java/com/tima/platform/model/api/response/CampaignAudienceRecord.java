package com.tima.platform.model.api.response;

import lombok.Builder;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Builder
public record CampaignAudienceRecord(List<String> size, List<String> gender, List<String> ageGroup,
                                     List<String> location, List<String> monthlyIncome) {}