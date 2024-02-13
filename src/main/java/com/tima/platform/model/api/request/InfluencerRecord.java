package com.tima.platform.model.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/8/24
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InfluencerRecord(String publicId, String username,
                               String fullName, String email,
                               String phoneNumber, String profilePicture,
                                Integer completed) {}
