package com.tima.platform.model.constant;

import lombok.Getter;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/12/24
 */
@Getter
public enum BookmarkType {
    CAMPAIGN("CAMPAIGN"),
    INFLUENCER("INFLUENCER");

    private final String type;

    BookmarkType(String type) {
        this.type = type;
    }
}

