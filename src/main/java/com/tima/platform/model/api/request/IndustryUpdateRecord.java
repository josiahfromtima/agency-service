package com.tima.platform.model.api.request;

import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/12/23
 */
@Builder
public record IndustryUpdateRecord(String oldName, String newName, String description) {}
