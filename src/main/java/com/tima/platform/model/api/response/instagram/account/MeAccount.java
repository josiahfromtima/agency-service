package com.tima.platform.model.api.response.instagram.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.tima.platform.model.api.response.instagram.pages.Page;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/18/24
 */
public record MeAccount(String id, String name, String category,
                        @JsonProperty("category_list")
                        @SerializedName("category_list")
                        List<Page> categoryList,
                        List<String> tasks) {}