package com.tima.platform.event;

import com.tima.platform.model.event.NotificationSetting;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Service
@RequiredArgsConstructor
public class NotificationSettingEvent {
    LoggerHelper log = LoggerHelper.newInstance(NotificationSettingEvent.class.getName());
    private final StreamBridge streamBridge;
    public Mono<Boolean> sendUpdateSettings(NotificationSetting setting) {
        log.info("Sending setting update request for ", setting.publicId());
        return Mono.just( streamBridge.send("setting-out-0", setting)
        );
    }
}
