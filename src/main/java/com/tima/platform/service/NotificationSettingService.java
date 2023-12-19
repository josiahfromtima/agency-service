package com.tima.platform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tima.platform.event.NotificationSettingEvent;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.event.NotificationSetting;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND_INFLUENCER;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Service
@RequiredArgsConstructor
public class NotificationSettingService {
    LoggerHelper log = LoggerHelper.newInstance(NotificationSettingService.class.getName());
    private final NotificationSettingEvent settingEvent;
    private static final String SETTING_MSG = "Setting request executed successfully";
    private static final String SENT_SETTING_MSG = "Sent Setting for Upate ";

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> requestNotificationSettingsUpdate(String publicId, JsonNode jNode) {

        return settingEvent.sendUpdateSettings(NotificationSetting.builder()
                        .publicId(publicId)
                        .campaignUpdateAlert(jNode.at("/campaignUpdateAlert").asBoolean())
                        .emailAlert(jNode.at("/emailAlert").asBoolean())
                        .paymentUpdateAlert(jNode.at("/paymentUpdateAlert").asBoolean())
                .build())
                .map(aBoolean -> AppUtil.buildAppResponse(
                        SENT_SETTING_MSG + (aBoolean ? "Successfully" : "Failed"), SETTING_MSG) );
    }
}
