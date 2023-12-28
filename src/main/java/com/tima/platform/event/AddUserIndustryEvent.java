package com.tima.platform.event;

import com.google.gson.Gson;
import com.tima.platform.model.api.response.ClientIndustryRecord;
import com.tima.platform.service.ClientIndustryService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/27/23
 */
@Configuration
@RequiredArgsConstructor
public class AddUserIndustryEvent {
    LoggerHelper log = LoggerHelper.newInstance(AddUserIndustryEvent.class.getName());
    private final ClientIndustryService industryService;

    @Bean
    public Consumer<String> userIndustry() {
        return s -> {
            log.info("User Industry --- {}", s);
            ClientIndustryRecord request = new Gson().fromJson(s, ClientIndustryRecord.class);
            if(request == null) return;
            industryService.addClientIndustry(request)
                    .subscribe(r -> log.info("Added User Industry: ", r));
        };

    }
}
