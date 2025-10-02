package com.example.MyGmail.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class UrlUtils {
    private UrlUtils() {}

    public static List<String> extractUrlsFromMail(String subject, String body, String sender) {
        String text = (subject == null ? "" : subject) + " "
                + (body == null ? "" : body) +  " "
                + (sender == null ? "" : sender);

        String[] words = text.split("\\s+");
        String urlRegex = "^https?://[^\\s]+$";
        Set<String> URLs = new HashSet<>();
        for (String w : words) {
            if (w.matches(urlRegex)) {
                URLs.add(w);
            }
        }
        return new ArrayList<>(URLs);
    }
}