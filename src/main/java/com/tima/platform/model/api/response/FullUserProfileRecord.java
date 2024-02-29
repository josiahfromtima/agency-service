package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/21/23
 */
@Builder
public record FullUserProfileRecord(String username, String publicId, UserProfileRecord profile) {}
