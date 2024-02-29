package com.tima.platform.repository.projection;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/27/23
 */
public interface NativeSql {
    String SELECT_CAMPAIGN_SEARCH_WITH_FILTER = "SELECT * FROM campaign_registration ";
    String TOP_CAMPAIGN_STATEMENT = "SELECT campaign_id AS campaign, COUNT(campaign_id) AS appearances" +
            " FROM influencer_application " +
            "GROUP BY campaign_id " +
            "ORDER BY appearances DESC " +
            "LIMIT :top";

    String CAMPAIGN_SEARCH_WITH_FILTER = SELECT_CAMPAIGN_SEARCH_WITH_FILTER +
            "WHERE lower(influencer_category) LIKE :category AND " +
            "lower(audience_size) LIKE :size AND " +
            "lower(audience_age_group) LIKE :age AND " +
            "lower(audience_location) LIKE :location ";

    String CAMPAIGN_SEARCH_2_WITH_FILTER = SELECT_CAMPAIGN_SEARCH_WITH_FILTER +
            "WHERE lower(influencer_category) LIKE :category AND " +
            "lower(audience_size) LIKE :size AND " +
            "lower(audience_age_group) LIKE :age AND " +
            "lower(audience_location) LIKE :location AND " +
            "lower(audience_gender) LIKE :gender AND " +
            "lower(social_media_platforms) LIKE :media AND " +
            "cost_per_post >= :cost";

    String RECOMMENDED_CAMPAIGN_STATEMENT = SELECT_CAMPAIGN_SEARCH_WITH_FILTER +
            "WHERE lower(influencer_category) LIKE :param1 OR " +
            "lower(influencer_category) LIKE :param2 OR " +
            "lower(influencer_category) LIKE :param3 ";
}
