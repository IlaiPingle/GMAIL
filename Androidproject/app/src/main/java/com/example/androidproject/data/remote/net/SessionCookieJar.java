package com.example.androidproject.data.remote.net;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public final class SessionCookieJar implements CookieJar {
    private static final String PREFS = "cookie_prefs";
    private static final String K_VALUE   = "token_value";
    private static final String K_DOMAIN  = "token_domain";
    private static final String K_PATH    = "token_path";
    private static final String K_EXPIRES = "token_expires";
    private static final String K_SECURE  = "token_secure";
    private static final String K_HTTPONLY= "token_httpOnly";
    private static final String K_HOSTONLY= "token_hostOnly";
    private static final String NAME = "token";

    private final SharedPreferences sp;

    public SessionCookieJar(Context ctx) {
        this.sp = ctx.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public synchronized void saveFromResponse(@NonNull HttpUrl url, List<Cookie> cookies) {
        for (Cookie c : cookies) {
            if (!NAME.equalsIgnoreCase(c.name())) continue;
            sp.edit()
                    .putString(K_VALUE, c.value())
                    .putString(K_DOMAIN, c.domain())
                    .putString(K_PATH,   c.path())
                    .putLong  (K_EXPIRES,c.expiresAt())
                    .putBoolean(K_SECURE,  c.secure())
                    .putBoolean(K_HTTPONLY,c.httpOnly())
                    .putBoolean(K_HOSTONLY,c.hostOnly())
                    .apply();
            break;
        }
    }

    @NonNull
    @Override
    public synchronized List<Cookie> loadForRequest(@NonNull HttpUrl url) {
        List<Cookie> out = new ArrayList<>();
        Cookie c = read();
        if (c == null) return out;
        long now = System.currentTimeMillis();
        if (c.expiresAt() <= now) { clear(); return out; }

        boolean domainOk = c.hostOnly() ? url.host().equalsIgnoreCase(c.domain())
                : url.host().equalsIgnoreCase(c.domain()) || url.host().endsWith("." + c.domain());
        boolean pathOk   = url.encodedPath().startsWith(c.path());
        boolean secureOk = !c.secure() || url.isHttps();

        if (domainOk && pathOk && secureOk) out.add(c);
        return out;
    }

    public synchronized boolean hasSession() {
        Cookie c = read();
        return c != null && c.expiresAt() > System.currentTimeMillis();
    }

    public synchronized void clear() {
        sp.edit().clear().apply();
    }

    private Cookie read() {
        String value  = sp.getString(K_VALUE,  null);
        String domain = sp.getString(K_DOMAIN, null);
        String path   = sp.getString(K_PATH,   "/");
        long   exp    = sp.getLong  (K_EXPIRES,0L);
        boolean sec   = sp.getBoolean(K_SECURE,false);
        boolean httpO = sp.getBoolean(K_HTTPONLY,true);
        boolean hostO = sp.getBoolean(K_HOSTONLY,false);
        if (value == null || domain == null || exp == 0L) return null;

        Cookie.Builder b = new Cookie.Builder()
                .name(NAME).value(value)
                .path(path).expiresAt(exp);
        if (hostO) b.hostOnlyDomain(domain); else b.domain(domain);
        if (sec)   b.secure();
        if (httpO) b.httpOnly();
        return b.build();
    }
}
