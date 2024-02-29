package com.tima.platform.model.api.request.analytics;

import com.tima.platform.model.api.response.FullUserProfileRecord;
import com.tima.platform.model.api.response.instagram.business.BasicBusinessInsight;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/25/24
 */
@Builder
public record InfluencerAnalytics(String applicationId,
                                  String userName,
                                  String profilePicture,
                                  String socialMediaPlatforms,
                                  LocalDate applicationDate,
                                  String userPublicId,
                                  BasicBusinessInsight insight) {}
