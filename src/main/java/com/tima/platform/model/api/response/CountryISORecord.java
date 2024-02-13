package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/6/24
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CountryISORecord(String name, String twoLetterCode, String threeLetterCode, Integer code) {}
