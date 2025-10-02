package com.example.MyGmail.util;

import com.google.gson.Gson;

import retrofit2.Response;

/**
 * Utility class for parsing API error responses.
 * Provides methods to extract meaningful error messages from Retrofit Response objects.
 * Assumes error responses are in JSON format with a "message" field.
 */
public final class ApiErrorParser {
    private ApiErrorParser() {
    }

    static class ErrorResponse {
        String message;
    }

    public static String parseMessage(Response<?> resp) {
        if (resp.errorBody() == null) return "Unknown error";
        try {
            String raw = resp.errorBody().string();
            try {
                Gson gson = new Gson();
                ErrorResponse er = gson.fromJson(raw, ErrorResponse.class);
                return (er != null && er.message != null && !er.message.isEmpty()) ? er.message : raw;
            } catch (Exception ignore) {
                return raw;
            }
        } catch (Exception e) {
            return "Failed to read error body";
        }
    }
}