package com.tima.platform.model.api.response.instagram.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tima.platform.model.api.response.instagram.insight.result.Breakdowns;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/2/24
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Breakdown(List<Breakdowns> breakdowns) {
}
