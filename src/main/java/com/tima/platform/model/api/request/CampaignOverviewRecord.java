package com.tima.platform.model.api.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Builder
public record CampaignOverviewRecord(@NotNull(message = "Campaign Name is Required")
                                     String name,
                                     @NotNull(message = "Brief Description is Required")
                                     String briefDescription,
                                     String website,
                                     @NotNull(message = "Planned Budget is Required")
                                     @PositiveOrZero( message = "Negative budget is not supported")
                                     BigDecimal plannedBudget,
                                     @NotNull(message = "Cost Per Post is Required")
                                     @PositiveOrZero(message = "Negative Cost is not supported")
                                     BigDecimal costPerPost,
                                     @NotNull(message = "Social Media List is Required")
                                     List<String> socialMediaPlatforms) {}