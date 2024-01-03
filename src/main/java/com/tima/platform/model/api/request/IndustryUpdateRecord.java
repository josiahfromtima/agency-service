package com.tima.platform.model.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/12/23
 */
@Builder
public record IndustryUpdateRecord(
        @NotNull(message = "Old Name is Required") String oldName,
        @NotNull(message = "New Name is Required") String newName,
        @NotNull(message = "Description is Required") String description) {}
