package com.tima.platform.event;

import com.tima.platform.model.api.request.AlertRecord;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.tima.platform.util.AppUtil.gsonInstance;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/31/24
 */
@Service
@RequiredArgsConstructor
public class AlertEvent {
    LoggerHelper log = LoggerHelper.newInstance(NotificationSettingEvent.class.getName());
    private final StreamBridge streamBridge;

    public Mono<Boolean> registerAlert(AlertRecord alertRecord) {
        log.info("send alert notification ", alertRecord.message());
        return Mono.just( streamBridge.send("alert-out-0", alertRecord)
        );
    }
}
