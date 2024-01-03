package com.tima.platform.model.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Builder
public record CampaignInfluencerRecord(@NotNull(message = "Influencer Category is Required")
                                       List< @NotBlank String> influencerCategory,
                                       @NotNull(message = "Audience Size is Required")
                                       List<@NotBlank String> audienceSize,
                                       @NotNull(message = "Audience Gender is Required")
                                       List<@NotBlank String> audienceGender,
                                       @NotNull(message = "Audience Age Group is Required")
                                       List<@NotBlank String> audienceAgeGroup,
                                       @NotNull(message = "Audience Location is Required")
                                       List<@NotBlank String> audienceLocation) {}