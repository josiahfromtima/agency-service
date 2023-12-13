package com.tima.platform.model.security;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/9/23
 */
public interface TimaAuthority {
    String ADMIN_INFLUENCER = "hasAnyAuthority('ADMIN','INFLUENCER')";
    String ADMIN = "hasAuthority('ADMIN')";
    String ADMIN_BRAND = "hasAnyAuthority('ADMIN','BRAND')";
    String ADMIN_BRAND_INFLUENCER = "hasAnyAuthority('ADMIN','BRAND', 'INFLUENCER')";
    String BRAND = "hasAuthority('BRAND')";
    String INFLUENCER = "hasAuthority('INFLUENCER')";
}
