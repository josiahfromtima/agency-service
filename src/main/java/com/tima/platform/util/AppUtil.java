package com.tima.platform.util;

import com.google.gson.Gson;
import com.tima.platform.model.api.AppResponse;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;
/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
public class AppUtil {
    private static final Random RANDOM2 = new Random(System.nanoTime());
    private static final String ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private AppUtil(){}
    public static boolean isNewRecord(Object id) {
        return Objects.isNull(id);
    }
    public static int userRole() {
        return 1;
    }

    public static String generateOTP(int digits) {
        StringBuilder otp = new StringBuilder();
        Random rand = new Random();
        while(digits > 0) {
            otp.append(rand.nextInt(10));
            digits--;
        }
        return otp.toString();
    }

    public static String generateReferenceNumber(String prefix) {

        if(prefix == null || prefix.length() < 3) return null;

        String extractedPrefix  = prefix.substring(0, 3);
        long currentTimeStamp = System.nanoTime();

        return (extractedPrefix.toUpperCase() + "-" + currentTimeStamp);
    }

    public static String generateIdAndSecrets(int num, String prefix){
        String prefix3Letters = (prefix == null || prefix.isEmpty()) || prefix.length() < 3?
                "SCR" : prefix.substring(0,3);

        String generatedCharacters = generateRandomCharacters(num, ALPHABETS);

        return prefix3Letters +
                "_" +
                generatedCharacters;
    }

    public static AppResponse buildAppResponse(Object data, String message) {
        return AppResponse.builder()
                .status(true)
                .data(data)
                .message(message)
                .build();
    }

    public static String getValueOrDefault(String account) {
        return Objects.isNull(account) ? "" : account;
    }
    public static String generateToken(int count) {
        StringBuilder token = new StringBuilder(UUID.randomUUID().toString());
        for(int i = 1; i <= count; i++) {
            token
                    .append("-")
                    .append(UUID.randomUUID());
        }
        return token.toString();
    }

    public static String generateRandomCharacters(int num, String characterSampleSpace){
        StringBuilder generatedString = new StringBuilder();
        for (int i = 0; i < num; i++) {
            char letter = (characterSampleSpace).charAt(RANDOM2.nextInt(characterSampleSpace.length()));
            generatedString.append(letter);
        }
        return generatedString.toString();
    }

    public static Gson gsonInstance() {
        return new Gson();
    }
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
