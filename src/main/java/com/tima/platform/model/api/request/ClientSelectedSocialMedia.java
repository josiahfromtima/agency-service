package com.tima.platform.model.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/25/24
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClientSelectedSocialMedia(@NotBlank(message = "Social Media Name is Required")
                                        String name,
                                        @NotBlank(message = "User handle or username is Required")
                                        String handle,
                                        String businessId,
                                        String accessToken,
                                        String logo,
                                        int expiresIn) {}
