package com.tima.platform.model.api.request;

import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/21/23
 */
@Builder
public record JwtRecord(String publicId, String token) {}
