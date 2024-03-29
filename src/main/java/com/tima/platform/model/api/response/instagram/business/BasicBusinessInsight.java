package com.tima.platform.model.api.response.instagram.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/19/24
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BasicBusinessInsight(String businessOwnerIgId,
                                   String businessIgId,
                                   String businessHandle,
                                   String businessName,
                                   String biography,
                                   String website,
                                   String profilePictureUrl,
                                   long followers,
                                   long totalMedia,
                                   long totalComments,
                                   long avgComments,
                                   long totalLikes,
                                   long avgLikes,
                                   BigDecimal avgEngagement) {}