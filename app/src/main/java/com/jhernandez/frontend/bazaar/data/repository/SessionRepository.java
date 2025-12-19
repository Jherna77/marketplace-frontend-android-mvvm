package com.jhernandez.frontend.bazaar.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.jhernandez.frontend.bazaar.data.api.ApiService;
import com.jhernandez.frontend.bazaar.data.model.LoginRequestDto;
import com.jhernandez.frontend.bazaar.data.model.LoginResponseDto;
import com.jhernandez.frontend.bazaar.data.network.CallbackDelegator;
import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TokenCallback;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.data.network.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository class for managing session-related operations.
 */
public class SessionRepository implements SessionRepositoryPort {

    private final SessionManager sessionManager;
    private final ApiService apiService;

    public SessionRepository(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.apiService = sessionManager.getApiService();
    }

    @Override
    public void init(TokenCallback callback) {
        if (sessionManager.hasToken()) {
            Log.d("SessionRepository", "Token found, validatingToken");
            apiService.validateToken().enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("SessionRepository", "Token is valid");
                        callback.onTokenValid();
                    } else {
                        Log.d("SessionRepository", "Token is not valid");
                        callback.onTokenExpired();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e("UserRepository", "Error validating token: " + t.getMessage());
                    callback.onError(CallbackDelegator.parseError(t));
                }
            });
        } else {
            Log.d("SessionRepository", "No token found, testing connection");
            apiService.testConnection().enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d("SessionRepository", "Connection successful");
                    callback.onTokenNotFound();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("UserRepository", "Error validating connection: " + t.getMessage());
                    callback.onError(CallbackDelegator.parseError(t));
                }
            });
        }
    }

    @Override
    public void login(String email, String password, SuccessCallback callback) {
        apiService.login(new LoginRequestDto(email, password))
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginResponseDto> call, @NonNull Response<LoginResponseDto> response) {
                        if (response.isSuccessful()) {
                            Log.d("SessionRepository", "Login successful");
                            assert response.body() != null;
                            sessionManager.saveToken(response.body().token());
                            callback.onSuccess();
                        } else {
                            Log.e("SessionRepository", "Login error: " + response.code());
                            callback.onError(CallbackDelegator.parseError(response));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LoginResponseDto> call, @NonNull Throwable t) {
                        Log.e("SessionRepository", "Login error: " + t.getMessage());
                        callback.onError(CallbackDelegator.parseError(t));
                    }
                });
    }

    @Override
    public void saveSessionUser(User user) { sessionManager.saveUser(user); }

    @Override
    public void logout() {
        sessionManager.logout();
    }

    @Override
    public User getSessionUser() {
        return sessionManager.getSessionUser();
    }

    @Override
    public Boolean isAuthenticated() {
        return getSessionUser() != null;
    }

    @Override
    public Boolean isAdmin() {
        return sessionManager.isAdmin();
    }

    @Override
    public Boolean isShop() {
        return sessionManager.isShop();
    }

}

