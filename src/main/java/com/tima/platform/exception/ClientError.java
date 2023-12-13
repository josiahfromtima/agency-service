package com.tima.platform.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Data
public class ClientError {
    @JsonProperty("error_description")
    @SerializedName("error_description")
    private String errorDescription;
    private String error;
    @JsonProperty("error_uri")
    @SerializedName("error_uri")
    private String errorUri;
}
