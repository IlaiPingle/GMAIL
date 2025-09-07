package com.example.androidproject.data.remote.net;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;


public class SessionCookieJar implements CookieJar {
    private static final String PREFS_NAME = "cookies_store";
    private static final String KEY_COOKIES = "all"; // single bucket is enough for one backend host
    private final SharedPreferences prefs;

    // In-memory cache
    private final Map<String, List<Cookie>> cache = new HashMap<>();

    public SessionCookieJar(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadFromPrefs();
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        // Merge cookies for this host
        List<Cookie> list = cache.getOrDefault(url.host(), new ArrayList<>());
        // remove same-name cookies
        for (Cookie newC : cookies) {
            for (Iterator<Cookie> it = list.iterator(); it.hasNext(); ) {
                Cookie old = it.next();
                if (old.name().equals(newC.name()) && domainMatches(old, newC.domain()) && old.path().equals(newC.path())) {
                    it.remove();
                }
            }
        }
        list.addAll(cookies);
        // remove expired
        long now = System.currentTimeMillis();
        list.removeIf(c -> c.expiresAt() < now);
        cache.put(url.host(), list);
        persistToPrefs();
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        long now = System.currentTimeMillis();
        List<Cookie> result = new ArrayList<>();
        for (Map.Entry<String, List<Cookie>> e : cache.entrySet()) {
            for (Cookie c : e.getValue()) {
                if (c.expiresAt() > now && cookieMatches(url, c)) {
                    result.add(c);
                }
            }
        }
        return result;
    }

    public synchronized void clear() {
        cache.clear();
        prefs.edit().remove(KEY_COOKIES).apply();
    }

    private boolean cookieMatches(HttpUrl url, Cookie c) {
        boolean domainOk = c.hostOnly() ? url.host().equals(c.domain()) : url.host().endsWith(c.domain());
        boolean pathOk = url.encodedPath().startsWith(c.path());
        boolean secureOk = !c.secure() || url.isHttps();
        return domainOk && pathOk && secureOk;
    }

    private boolean domainMatches(Cookie c, String domain) {
        return c.hostOnly() ? c.domain().equals(domain) : domain.endsWith(c.domain());
    }

    private void persistToPrefs() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<Cookie>> e : cache.entrySet()) {
            for (Cookie c : e.getValue()) {
                sb.append(serialize(c)).append("\n");
            }
        }
        prefs.edit().putString(KEY_COOKIES, sb.toString()).apply();
    }

    private void loadFromPrefs() {
        String s = prefs.getString(KEY_COOKIES, null);
        if (s == null || s.isEmpty()) return;
        long now = System.currentTimeMillis();
        for (String line : s.split("\n")) {
            if (line.trim().isEmpty()) continue;
            Cookie c = deserialize(line);
            if (c == null || c.expiresAt() <= now) continue;
            List<Cookie> list = cache.getOrDefault(c.domain(), new ArrayList<>());
            list.add(c);
            cache.put(c.domain(), list);
        }
    }

    // Simple serialization
    private String serialize(Cookie c) {
        return String.join("\t",
                c.name(),
                c.value(),
                c.domain(),
                c.path(),
                String.valueOf(c.expiresAt()),
                c.secure() ? "1" : "0",
                c.httpOnly() ? "1" : "0",
                c.hostOnly() ? "1" : "0",
                c.persistent() ? "1" : "0"
        );
    }

    private Cookie deserialize(String s) {
        try {
            String[] p = s.split("\t");
            Cookie.Builder b = new Cookie.Builder()
                    .name(p[0])
                    .value(p[1])
                    .path(p[3]);
            if ("1".equals(p[7])) b.hostOnlyDomain(p[2]); else b.domain(p[2]);
            b.expiresAt(Long.parseLong(p[4]));
            if ("1".equals(p[5])) b.secure();
            if ("1".equals(p[6])) b.httpOnly();
            return b.build();
        } catch (Exception e) {
            return null;
        }
    }
}