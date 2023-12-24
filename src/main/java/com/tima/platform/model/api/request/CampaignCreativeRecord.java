package com.tima.platform.model.api.request;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Builder
public record CampaignCreativeRecord(String paymentType, LocalDate startDate, LocalDate endDate,
                                     String contentType, String contentPlacement, String creativeBrief,
                                     String rules, String creativeTone, String referenceLink,
                                     List<String> awarenessObjective, List<String> acquisitionObjective,
                                     String thumbnail, Boolean visibility) {}