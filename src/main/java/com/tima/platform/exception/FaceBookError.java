package com.tima.platform.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/25/24
 */
@Data
public class FaceBookError {
    private String message;
    private String type;
    private Integer code;
    @JsonProperty("fbtrace_id")
    @SerializedName("fbtrace_id")
    private String traceId;
}
