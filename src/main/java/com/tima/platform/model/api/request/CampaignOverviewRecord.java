package com.tima.platform.model.api.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Builder
public record CampaignOverviewRecord(String name, String briefDescription, String website,
                                     BigDecimal plannedBudget, BigDecimal costPerPost, List<String> socialMediaPlatforms) {}