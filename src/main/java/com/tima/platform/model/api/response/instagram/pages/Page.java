package com.tima.platform.model.api.response.instagram.pages;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/18/24
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Page(String id, String name) {}
