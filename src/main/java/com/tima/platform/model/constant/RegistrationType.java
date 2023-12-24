package com.tima.platform.model.constant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/20/23
 */
public enum RegistrationType {
    OVERVIEW("OVERVIEW"),
    INFLUENCER("INFLUENCER"),
    CREATIVE("CREATIVE");

    private final String type;

    RegistrationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
