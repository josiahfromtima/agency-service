package com.tima.platform.model.api.response.instagram;

import com.tima.platform.model.api.response.instagram.pagination.Paging;
import lombok.Builder;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/18/24
 */
@Builder
public record GraphApi<T>(List<T> data, Paging paging) {}
