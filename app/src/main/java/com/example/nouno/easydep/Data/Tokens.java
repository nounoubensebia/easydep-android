package com.example.nouno.easydep.Data;

import com.auth0.android.jwt.JWT;

import java.util.Calendar;

/**
 * Created by nouno on 13/05/2017.
 */

public class Tokens {
    private String accessToken;
    private String refreshToken;

    public Tokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public boolean accessTokenExpired()
    {
        JWT jwt = new JWT(accessToken);
        long time = jwt.getExpiresAt().getTime();
        long time2 = System.currentTimeMillis()+3600000;
        long offset = time2-time;
        if (offset>0)
            return true;
        else
            return false;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
