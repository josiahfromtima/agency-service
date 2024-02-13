package com.tima.platform.util;

import lombok.Data;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/13/24
 */
@Data
public class InfluencerSearchSetting {
    private String email;
    private String name;
    private String username;

    private InfluencerSearchSetting() {}

    public static InfluencerSearchSetting instance() {
        return new InfluencerSearchSetting();
    }

    public InfluencerSearchSetting email(String email) {
        this.email = email;
        return this;
    }

    public InfluencerSearchSetting name(String name) {
        this.name = name;
        return this;
    }

    public InfluencerSearchSetting username(String username) {
        this.username = username;
        return this;
    }

}
