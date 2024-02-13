package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/21/23
 */
public record FullUserProfileRecord(String username, String publicId, UserProfileRecord profile) {}
