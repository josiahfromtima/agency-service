package com.tima.platform.repository;

import com.tima.platform.domain.CampaignAudience;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
public interface CampaignAudienceRepository extends ReactiveCrudRepository<CampaignAudience, Integer> {
}
