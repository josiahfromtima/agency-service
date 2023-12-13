package com.tima.platform.model.event;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Data
@Builder
public class GenericRequest {
    private String to;
    private String templateId;
    private Object template;

}
