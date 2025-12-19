package com.jhernandez.frontend.bazaar.domain.port;

import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.User;

import java.util.List;

/*
 * Interface representing the UserRepositoryPort.
 */
public interface UserRepositoryPort {

    void createUser(User user, SuccessCallback callback);
    void findAllUsers(TypeCallback<List<User>> callback);
    void findUserById(Long id, TypeCallback<User> callback);
    void findUserByEmail(String email, TypeCallback<User> callback);
    void updateUser(User user, SuccessCallback callback);
    void enableUserById(Long id, SuccessCallback callback);
    void disableUserById(Long id, SuccessCallback callback);

}
