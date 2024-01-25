package com.tima.platform.model.api.response.instagram.token;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/19/24
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LongLivedAccessToken(@JsonProperty("access_token")
                          @SerializedName("access_token")
                          String accessToken,
                                   @JsonProperty("token_type")
                          @SerializedName("token_type")
                          String tokenType,
                                   @JsonProperty("expires_in")
                          @SerializedName("expires_in")
                          long expiresIn) {}