package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tima.platform.model.constant.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/20/23
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
@JsonIgnoreProperties
public record InfluencerApplicationRecord(String applicationId,
                                          @NotBlank(message = "Empty Campaign Public Id")
                                          String campaignPublicId,
                                          String campaignName,
                                          BigDecimal campaignBudget, String campaignDescription, String username,
                                          String fullName, String email, String phoneNumber, String profilePicture,

                                          String socialMediaPlatform, String collaboration, String userExperience,
                                          String userExperienceBrief, String userMotivationBrief, ApplicationStatus status,
                                          LocalDate applicationDate, String approvedBy, String submittedBy,
                                          String campaignLogo,
                                          String reviewedBy, Instant createdOn, Instant editedOn) {}