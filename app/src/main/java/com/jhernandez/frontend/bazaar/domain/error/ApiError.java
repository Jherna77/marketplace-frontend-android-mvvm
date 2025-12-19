package com.jhernandez.frontend.bazaar.domain.error;

/*
 * Enumeration of API error types.
 */
public enum ApiError {
    BAD_REQUEST_ERROR,
    UNAUTHORIZED_ERROR,
    FORBIDDEN_ERROR,
    NOT_FOUND_ERROR,
    INTERNAL_SERVER_ERROR,
    NETWORK_ERROR,
    UNKNOWN_ERROR;

    public static ApiError fromCode(Integer errorCode) {
        return switch (errorCode) {
            case 400 -> BAD_REQUEST_ERROR;
            case 401 -> UNAUTHORIZED_ERROR;
            case 403 -> FORBIDDEN_ERROR;
            case 404 -> NOT_FOUND_ERROR;
            case 500 -> INTERNAL_SERVER_ERROR;
            default -> UNKNOWN_ERROR;
        };
    }
}
