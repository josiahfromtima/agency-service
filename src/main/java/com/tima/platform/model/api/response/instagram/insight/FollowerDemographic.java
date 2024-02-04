package com.tima.platform.model.api.response.instagram.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tima.platform.model.api.response.instagram.GraphApi;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/2/24
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FollowerDemographic(GraphApi<Demographic> data) {}
