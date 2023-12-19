package com.tima.platform.model.event;

import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Builder
public record NotificationSetting(String publicId, boolean campaignUpdateAlert, boolean emailAlert, boolean paymentUpdateAlert) {
}
