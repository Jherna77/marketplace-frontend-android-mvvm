package com.jhernandez.frontend.bazaar.data.repository;

import com.jhernandez.frontend.bazaar.data.api.ApiService;
import com.jhernandez.frontend.bazaar.data.mapper.UserMapper;
import com.jhernandez.frontend.bazaar.data.network.CallbackDelegator;
import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.port.UserRepositoryPort;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * Repository class for managing user-related operations.
 */
@RequiredArgsConstructor
public class UserRepository implements UserRepositoryPort {

    private final ApiService apiService;

    @Override
    public void createUser(User user, SuccessCallback callback) {
        apiService.createUser(UserMapper.toDto(user))
                .enqueue(CallbackDelegator.delegate("register", callback));
    }

    @Override
    public void findAllUsers(TypeCallback<List<User>> callback) {
        apiService.findAllUsers()
                .enqueue(CallbackDelegator.delegate(
                        "loadUsers",
                        response ->
                                callback.onSuccess(UserMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void findUserById(Long id, TypeCallback<User> callback) {
        apiService.findUserById(id)
                .enqueue(CallbackDelegator.delegate(
                        "findUserById",
                        response ->
                                callback.onSuccess(UserMapper.toDomain(response)),
                                callback::onError)
                );
    }

    @Override
    public void findUserByEmail(String email, TypeCallback<User> callback) {
        apiService.findUserByEmail(email)
                .enqueue(CallbackDelegator.delegate(
                        "findUserByEmail",
                        response ->
                                callback.onSuccess(UserMapper.toDomain(response)),
                                callback::onError));
    }

    @Override
    public void updateUser(User user, SuccessCallback callback) {
        apiService.updateUser(user.id(), UserMapper.toDto(user))
                .enqueue(CallbackDelegator.delegate("updateUser", callback));
    }

    @Override
    public void enableUserById(Long id, SuccessCallback callback) {
        apiService.enableUser(id)
                .enqueue(CallbackDelegator.delegate("enableUser", callback));
    }

    @Override
    public void disableUserById(Long id, SuccessCallback callback) {
        apiService.disableUser(id)
                .enqueue(CallbackDelegator.delegate("disableUser", callback));
    }

}