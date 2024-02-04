package com.tima.platform.model.api.response.instagram.insight.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/2/24
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Breakdowns(@JsonProperty("dimension_values")
                         @SerializedName("dimension_values")
                         List<String> keys,
                         Results results) {}
