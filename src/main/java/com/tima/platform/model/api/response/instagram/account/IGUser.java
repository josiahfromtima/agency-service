package com.tima.platform.model.api.response.instagram.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/18/24
 */
public record IGUser(
        @JsonProperty("instagram_business_account")
        @SerializedName("instagram_business_account")
        BusinessAccount businessAccount,
        String id) {}
