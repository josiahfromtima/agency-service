package com.tima.platform.model.api.response.instagram.business;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/18/24
 */
public record BusinessSummary(String id,
                              @JsonProperty("business_discovery")
                              @SerializedName("business_discovery")
                              BusinessDiscovery businessDiscovery) {
}
