package com.jhernandez.frontend.bazaar.data.network;

import static com.jhernandez.frontend.bazaar.data.network.JsonUtils.gson;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ADMIN;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_USER;
import static com.jhernandez.frontend.bazaar.core.constants.Values.BASE_URL;
import static com.jhernandez.frontend.bazaar.core.constants.Values.SHOP;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.auth0.jwt.JWT;
import com.jhernandez.frontend.bazaar.data.api.ApiService;
import com.jhernandez.frontend.bazaar.domain.model.User;

import java.util.concurrent.TimeUnit;

import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * SessionManager class for managing user sessions, tokens, and ApiService instance.
 */
public class SessionManager {
    private static final String SUBJECT = "sub";
    private static final String AUTH_PREFERENCES = "auth";
    private static final String ARG_JWT_TOKEN = "jwt_token";

    private static SessionManager instance;
    private final SharedPreferences sharedPreferences;

    // Get ApiService from SessionManager (Singleton)
    @Getter
    private final ApiService apiService;

    private boolean hasToken;

    @Getter
    private User sessionUser;

    private SessionManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(AUTH_PREFERENCES, Context.MODE_PRIVATE);
        sessionUser = getUser();
        hasToken = getToken() != null;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(this))
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        this.apiService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            Log.d("SessionManager", "Creating SessionManager instance");
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    // Save token to SharedPreferences
    public void saveToken(String token) {
        Log.d("SessionManager", "Saving token to SharedPreferences");
        sharedPreferences.edit().putString(ARG_JWT_TOKEN, token).apply();
        hasToken = true;
    }

    // Get token from SharedPreferences
    public String getToken() {
        Log.d("SessionManager", "Getting token from SharedPreferences");
        return sharedPreferences.getString(ARG_JWT_TOKEN, null);
    }

    // Clear token from SharedPreferences
    public void clearToken() {
        Log.d("SessionManager", "Clearing token from SharedPreferences");
        sharedPreferences.edit().remove(ARG_JWT_TOKEN).apply();
        hasToken = false;
    }

    public Boolean hasToken() {
        Log.d("SessionManager", "Checking if token exists: " + hasToken);
        return hasToken;
    }

    // Save User object to SharedPreferences
    public void saveUser(User user) {
        Log.d("SessionManager", "Saving user to SharedPreferences");
        sharedPreferences.edit().putString(ARG_USER, gson.toJson(user)).apply();
        sessionUser = user;
    }

    // Get User object from SharedPreferences
    private User getUser() {
        Log.d("SessionManager", "Getting user from SharedPreferences");
        String userJson = sharedPreferences.getString(ARG_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    // Clear user data from SharedPreferences
    public void clearUser() {
        Log.d("SessionManager", "Clearing user from SharedPreferences");
        sharedPreferences.edit().remove(ARG_USER).apply();
        sessionUser = null;
    }

    public void logout() {
        clearToken();
        clearUser();
    }

    // Extract user email from token
    public String getUserEmail() {
        Log.d("SessionManager", "Extracting user email from token");
        try {
            return JWT.decode(getToken()).getClaim(SUBJECT).asString();
        } catch (Exception e) {
            Log.e("SessionManager", "Error extracting user email from token", e);
            return null;
        }
    }

    public Boolean isAdmin() {
        return sessionUser != null && ADMIN.equals(sessionUser.role().name());
    }

    public Boolean isShop() {
        return sessionUser != null && SHOP.equals(sessionUser.role().name());
    }

}
