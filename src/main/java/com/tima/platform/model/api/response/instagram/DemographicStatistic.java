package com.tima.platform.model.api.response.instagram;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/5/24
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public record DemographicStatistic(String name, long value1, long value2, long value3) {}
