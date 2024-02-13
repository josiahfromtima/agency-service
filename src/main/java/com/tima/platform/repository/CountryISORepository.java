package com.tima.platform.repository;

import com.tima.platform.domain.CountryISO;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/6/24
 */
public interface CountryISORepository extends ReactiveCrudRepository<CountryISO, Integer> {
}
