package com.tima.platform.model.api.response;

import lombok.Builder;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Builder
public record CampaignCreativeRecord(List<String> contentType, List<String> contentPlacement, List<String> creativeTone,
                                     List<String> objectiveAwareness, List<String> objectiveAcquisition) {}
