package com.tima.platform.model.api.response.instagram.business;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.tima.platform.model.api.response.instagram.GraphApi;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/18/24
 */
public record BusinessDiscovery(String id,
                                String username,
                                String name,
                                String biography,
                                String website,
                                @JsonProperty("followers_count")
                                @SerializedName("followers_count")
                                long followersCount,
                                @JsonProperty("profile_picture_url")
                                @SerializedName("profile_picture_url")
                                String profilePictureUrl,
                                @JsonProperty("media_count")
                                @SerializedName("media_count")
                                long mediaCount,
                                GraphApi<MediaItem> media) {}
