package com.tima.platform.model.api.response.instagram.pagination;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/18/24
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Cursor(String before, String after) {}
