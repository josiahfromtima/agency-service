package com.tima.platform.model.api.response.instagram.business;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/18/24
 */
public record MediaItem(String id,
                        @JsonProperty("comments_count")
                        @SerializedName("comments_count")
                        long commentsCount,
                        @JsonProperty("like_count")
                        @SerializedName("like_count")
                        long likeCount,
                        @JsonProperty("media_product_type")
                        @SerializedName("media_product_type")
                        String mediaProductType,
                        @JsonProperty("media_type")
                        @SerializedName("media_type")
                        String mediaType) {}