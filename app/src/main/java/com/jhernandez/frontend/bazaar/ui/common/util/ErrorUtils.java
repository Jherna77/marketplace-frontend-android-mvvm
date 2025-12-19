package com.jhernandez.frontend.bazaar.ui.common.util;

import android.content.Context;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.domain.error.ApiError;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.error.ValidationError;

/* Utility class for handling error messages.
 * Provides methods to get user-friendly error messages based on different error types.
 * It maps ValidationError and ApiError enums to string resources. 
 */
public class ErrorUtils {

    public static String getMessage(Context context, ValidationError error) {
        return context.getString(getResId(error));
    }

    public static String getMessage(Context context, ApiError error) {
        return context.getString(getResId(error));
    }

    public static String getMessage(Context context, ApiErrorResponse response) {
        return response.getApiError() == ApiError.NETWORK_ERROR
                ? context.getString(R.string.network_error_msg)
                : response.getMessage() == null
                ? getMessage(context, response.getApiError())
                : response.getMessage();
    }

    private static int getResId(ValidationError error) {
        return switch (error) {
            case FIELD_EMPTY -> R.string.required_field_error;
            case ROLE_NOT_SELECTED -> R.string.select_user_role;
            case PASSWORDS_DO_NOT_MATCH -> R.string.password_match_error;
            case TERMS_NOT_ACCEPTED -> R.string.unchecked_terms_error;
            case CATEGORY_NOT_SELECTED -> R.string.select_category_msg;
            case NO_IMAGES -> R.string.select_image_msg;
            case QUERY_EMPTY -> R.string.required_query_error;
            case INVALID_PRICE -> R.string.invalid_price_msg;
            case INVALID_SHIPPING -> R.string.invalid_shipping_msg;
            case INVALID_DISCOUNT_PRICE -> R.string.invalid_discount_price_msg;
            case RATING_EMPTY -> R.string.select_rating_msg;
            default -> R.string.unknown_error;
        };
    }

    private static int getResId(ApiError error) {
        return switch (error) {
            case BAD_REQUEST_ERROR -> R.string.bad_request_error;
            case UNAUTHORIZED_ERROR -> R.string.unauthorized_error;
            case FORBIDDEN_ERROR -> R.string.forbidden_error;
            case NOT_FOUND_ERROR -> R.string.not_found_error;
            case INTERNAL_SERVER_ERROR -> R.string.server_error;
            case NETWORK_ERROR -> R.string.network_error;
            default -> R.string.unknown_error;
        };
    }

}
