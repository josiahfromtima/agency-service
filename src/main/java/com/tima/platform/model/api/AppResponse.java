package com.tima.platform.model.api;

import lombok.Builder;
import lombok.Data;
/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Data
@Builder
public class AppResponse {
    private boolean status;
    private String message;
    private Object data;

}
