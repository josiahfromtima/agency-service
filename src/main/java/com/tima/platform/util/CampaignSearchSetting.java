package com.tima.platform.util;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/24/23
 */
@Data
public class CampaignSearchSetting {
    private String category;
    private String type;
    private BigDecimal lowerBoundBudget;
    private BigDecimal upperBoundBudget;
    private String audience;
    private String status;

    private CampaignSearchSetting() {}

    public static CampaignSearchSetting instance() {
        return new CampaignSearchSetting();
    }

    public CampaignSearchSetting category(String category) {
        this.category = category;
        return this;
    }

    public CampaignSearchSetting type(String type) {
        this.type = type;
        return this;
    }

    public CampaignSearchSetting lowerBoundBudget(BigDecimal lowerBoundBudget) {
        this.lowerBoundBudget = lowerBoundBudget;
        return this;
    }

    public CampaignSearchSetting upperBoundBudget(BigDecimal upperBoundBudget) {
        this.upperBoundBudget = upperBoundBudget;
        return this;
    }
    public CampaignSearchSetting audience(String audience) {
        this.audience = audience;
        return this;
    }
    public CampaignSearchSetting status(String status) {
        this.status = status;
        return this;
    }
}
