package com.tima.platform.model.api.request.analytics;

import lombok.Builder;

import java.util.Map;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/25/24
 */
@Builder
public record PairMap(Map<String, String> alphaMap, Map<String, Long> numbericMap) {}
