package com.jhernandez.frontend.bazaar.domain.port;

import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.UserRole;

import java.util.List;

/*
 * Interface representing the UserRoleRepositoryPort.
 */
public interface UserRoleRepositoryPort {

    void findAllUserRoles(TypeCallback<List<UserRole>> callback);

}
