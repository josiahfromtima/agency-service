package com.tima.platform.model.api.response.campaign;

import lombok.Builder;

import java.time.LocalDate;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/29/24
 */
@Builder
public record PastExperience(String campaignName, String campaignBanner, LocalDate startDate, LocalDate endDate) {}
