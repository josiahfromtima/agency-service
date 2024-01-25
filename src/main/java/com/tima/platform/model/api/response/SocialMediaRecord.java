package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/25/24
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SocialMediaRecord(@NotBlank(message = "Social Media Name is required")
                                String name,
                                String logo,
                                @NotBlank(message = "Access Token or API Key is required")
                                String accessToken,
                                Integer expiresIn, Instant createdOn, Instant expiresOn) {}