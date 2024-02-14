package com.tima.platform.model.api.response.instagram.insight.metrics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/13/24
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record InsightMetrics(String id, String name, String period, String title, String description,
                             @JsonProperty("total_value")
                             @SerializedName("total_value")
                             MetricType totalValue) {}
