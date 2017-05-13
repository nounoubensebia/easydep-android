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
        return jwt.isExpired(Calendar.getInstance().get(Calendar.SECOND));
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
