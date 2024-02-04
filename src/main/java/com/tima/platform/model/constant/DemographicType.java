package com.tima.platform.model.constant;

import lombok.Getter;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/2/24
 */
@Getter
public enum DemographicType {
    AGE_GENDER("AGE_GENDER"),
    COUNTRY("COUNTRY"),
    CITY("CITY");

    private final String type;

    DemographicType(String type) {
        this.type = type;
    }

}
