package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/1/24
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BookmarkRecord(@NotBlank(message = "Title is required")
                             String title,
                             String bookmarkPublicId,
                             Instant createdOn) {}