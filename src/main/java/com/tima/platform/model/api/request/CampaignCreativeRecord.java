package com.tima.platform.model.api.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Builder
public record CampaignCreativeRecord( @NotNull(message = "Payment Type is Required")
                                      String paymentType,
                                     @NotNull(message = "Start Date is Required")
                                     @FutureOrPresent(message = "Wrong Start Date. [Format: YYYY-MM-DD]. " +
                                             "Should be today or future date")
                                     LocalDate startDate,
                                      @NotNull(message = "End Date is Required")
                                      @FutureOrPresent(message = "Wrong End Date. [Format: YYYY-MM-DD]. " +
                                              "Should be today or future date")
                                      LocalDate endDate,
                                     @NotNull(message = "Content Type is Required")
                                      String contentType,
                                     @NotNull(message = "Content Placement is Required")
                                      String contentPlacement,
                                     @NotNull(message = "Creative Brief is Required")
                                      String creativeBrief,
                                     @NotNull(message = "Rules is Required")
                                      String rules,
                                     @NotNull(message = "Creative Tone is Required")
                                      String creativeTone,
                                     String referenceLink,
                                     @NotNull(message = "Awareness Object is Required")
                                      List<@NotBlank String> awarenessObjective,
                                     @NotNull(message = "Acquisition Object is Required")
                                      List<@NotBlank String> acquisitionObjective,
                                     @NotNull(message = "Thumbnail name is Required")
                                      String thumbnail,
                                     Boolean visibility) {}