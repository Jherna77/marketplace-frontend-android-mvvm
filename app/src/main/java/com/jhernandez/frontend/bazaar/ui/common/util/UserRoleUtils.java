package com.jhernandez.frontend.bazaar.ui.common.util;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ADMIN;
import static com.jhernandez.frontend.bazaar.core.constants.Values.CUSTOMER;
import static com.jhernandez.frontend.bazaar.core.constants.Values.SHOP;

import android.content.Context;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.domain.model.UserRole;

/* Utility class for handling user role labels.
 * Provides methods to get user-friendly labels for different user roles.
 * It maps UserRole enums to string resources.
 */
public class UserRoleUtils {

    public static String getLabel(Context context, UserRole userRole) {
        return context.getString(getResId(userRole.name()));
    }

    private static int getResId(String roleName) {
        return switch (roleName) {
            case ADMIN -> R.string.role_admin;
            case SHOP -> R.string.role_shop;
            case CUSTOMER -> R.string.role_customer;
            default -> R.string.unknown_role;
        };
    }

}
