package com.tima.platform.util;

import lombok.Builder;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/27/24
 */
@Builder
public record GenderAtomicLong(AtomicLong male, AtomicLong female) {}
