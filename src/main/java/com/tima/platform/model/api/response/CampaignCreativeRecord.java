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
public record CampaignCreativeRecord(
        @NotNull(message = "Content Type is Required")
        List<String> contentType,
        @NotNull(message = "Content Placement is Required")
        List<String> contentPlacement,
        @NotNull(message = "Creative Tone is Required")
        List<String> creativeTone,
        @NotNull(message = "Objective Awareness is Required")
        List<String> objectiveAwareness,
        @NotNull(message = "Objective Acquisition is Required")
        List<String> objectiveAcquisition) {}
