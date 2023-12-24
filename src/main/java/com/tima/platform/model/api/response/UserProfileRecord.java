package com.tima.platform.model.api.response;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/21/23
 */
public record UserProfileRecord(String firstName, String middleName, String lastName, String email,
                                String phoneNumber, String userType, String companyName, String website,
                                String language, String profilePicture, String registeredDocument) {}
