package com.tima.platform.model.constant;

import lombok.Getter;

import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/31/24
 */
@Getter
public enum AlertType {
    CAMPAIGN("CAMPAIGN"),
    NEW("A New application for the campaign %s was submitted! [%s]"),
    REVIEW("The %s application has ben reviewed"),
    COMPLETED("%s campaign is completed"),
    PROGRESS("%s campaign is %d completed"),
    STARTED("New Campaign Started");

    private final String type;

    AlertType(String type) {
        this.type = type;
    }


    public String getTitle(String name, String param) {
        if(NEW.name().equals(name)) return String.format("%s Campaign Application Received", param);
        else if(REVIEW.name().equals(name)) return String.format("%s Application Reviewed", param);
        else if(COMPLETED.name().equals(name)) return String.format("%s Campaign Successful", param);
        else if(PROGRESS.name().equals(name)) return String.format("%s Campaign in Progress", param);
        else if(STARTED.name().equals(name)) return String.format("%s Campaign Started", param);
        else return "";
    }

    public String getMessage(String message, Object... param) {
        if(Objects.isNull(param)) return message;
        return String.format(message, param);
    }
}
