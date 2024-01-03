package com.tima.platform.model.api.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Builder
public record CampaignAudienceRecord(
        @NotNull(message = "Audience size is Required")
        List<String> size,
        @NotNull(message = "Audience gender is Required")
        List<String> gender,
        @NotNull(message = "Audience Age Group is Required")
        List<String> ageGroup,
        @NotNull(message = "Audience location is Required")
        List<String> location,
        @NotNull(message = "Audience Monthly Income is Required")
        List<String> monthlyIncome) {}