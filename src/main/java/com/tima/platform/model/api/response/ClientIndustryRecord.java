package com.tima.platform.model.api.response;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/26/23
 */
@Builder
public record ClientIndustryRecord(String userPublicId, List<String> selectedIndustries) {}
