package com.tima.platform.util;

import lombok.Data;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/24/23
 */
@Data
public class CampaignSearchSetting {
    private String category;
    private String audienceSize;
    private String audienceAge;
    private String audienceLocation;

    private CampaignSearchSetting() {}

    public static CampaignSearchSetting instance() {
        return new CampaignSearchSetting();
    }

    public CampaignSearchSetting category(String category) {
        this.category = category;
        return this;
    }

    public CampaignSearchSetting audienceSize(String audienceSize) {
        this.audienceSize = audienceSize;
        return this;
    }

    public CampaignSearchSetting audienceAge(String audienceAge) {
        this.audienceAge = audienceAge;
        return this;
    }

    public CampaignSearchSetting audienceLocation(String audienceLocation) {
        this.audienceLocation = audienceLocation;
        return this;
    }

}
