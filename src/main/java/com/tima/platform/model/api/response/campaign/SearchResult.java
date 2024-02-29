package com.tima.platform.model.api.response.campaign;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/29/24
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SearchResult(String campaignId, String influencerPublicId, String name,
                           String banner, String profilePicture, String description) {}
