package com.jhernandez.frontend.bazaar.domain.error;

/*
 * Class representing an API error response.
 */
public class ApiErrorResponse {
    private final ApiError apiError;
    private final String message;

    public ApiErrorResponse(ApiError apiError, String message) {
        this.apiError = apiError;
        this.message = message;
    }

    public ApiError getApiError() {
        return apiError;
    }

    public String getMessage() {
        return message;
    }

}
