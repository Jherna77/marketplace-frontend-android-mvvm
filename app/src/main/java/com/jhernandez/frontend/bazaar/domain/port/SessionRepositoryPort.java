package com.jhernandez.frontend.bazaar.domain.port;

import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TokenCallback;
import com.jhernandez.frontend.bazaar.domain.model.User;

/*
 * Interface representing the SessionRepositoryPort.
 */
public interface SessionRepositoryPort {

    void init(TokenCallback callback);
    void login(String email, String password, SuccessCallback callback);
    void saveSessionUser(User user);
    void logout();
    User getSessionUser();
    Boolean isAuthenticated();
    Boolean isAdmin();
    Boolean isShop();
}
