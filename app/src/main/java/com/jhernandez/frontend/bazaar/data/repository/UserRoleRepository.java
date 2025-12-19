package com.jhernandez.frontend.bazaar.data.repository;

import com.jhernandez.frontend.bazaar.data.api.ApiService;
import com.jhernandez.frontend.bazaar.data.mapper.UserRoleMapper;
import com.jhernandez.frontend.bazaar.data.network.CallbackDelegator;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.UserRole;
import com.jhernandez.frontend.bazaar.domain.port.UserRoleRepositoryPort;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * Repository class for managing user role-related operations.
 */
@RequiredArgsConstructor
public class UserRoleRepository implements UserRoleRepositoryPort {

    private final ApiService apiService;

    @Override
    public void findAllUserRoles(TypeCallback<List<UserRole>> callback) {
        apiService.findAllUserRoles()
                .enqueue(CallbackDelegator.delegate(
                        "findAllUserRoles",
                        response ->
                                callback.onSuccess(UserRoleMapper.toDomainList(response)),
                                callback::onError));
    }

}
