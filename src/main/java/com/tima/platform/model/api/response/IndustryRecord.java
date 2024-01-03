package com.tima.platform.model.api.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/12/23
 */
@Builder
public record IndustryRecord(
        @NotNull(message = "Name is Required")
        String name,
        @NotNull(message = "Description is Required")
        String description) {}
