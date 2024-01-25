package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tima.platform.model.api.request.ClientSelectedSocialMedia;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/25/24
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClientSocialMediaRecord(String userId, List<ClientSelectedSocialMedia> selectedSocialMedia,
                                      String url, Instant createdOn) {}