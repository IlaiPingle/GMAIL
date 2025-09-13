package com.example.androidproject.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class UrlUtils {
    private UrlUtils() {}

    public static List<String> extractUrlsLikeFrontend(String subject, String body, String sender) {
        String text = (subject == null ? "" : subject) + " "
                + (body == null ? "" : body) + " sender: "
                + (sender == null ? "" : sender);

        String[] words = text.split("\\s+");
        String urlRegex = "^https?://[^\\s]+$";
        Set<String> unique = new HashSet<>();
        for (String w : words) {
            if (w.matches(urlRegex)) {
                unique.add(w);
            }
        }
        return new ArrayList<>(unique);
    }
}