package com.tima.platform.repository.projection;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/27/23
 */
public interface NativeSql {
    String TOP_CAMPAIGN_STATEMENT = "SELECT campaign_id AS campaign, COUNT(campaign_id) AS appearances" +
            " FROM influencer_application " +
            "GROUP BY campaign_id " +
            "ORDER BY appearances DESC " +
            "LIMIT :top";

    String CAMPAIGN_SEARCH_WITH_FILTER = "SELECT * FROM campaign_registration " +
            "WHERE lower(influencer_category) LIKE :category OR " +
            "lower(audience_size) LIKE :size OR " +
            "lower(audience_age_group) LIKE :age OR " +
            "lower(audience_location) LIKE :location ";
}
