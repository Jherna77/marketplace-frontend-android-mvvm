package com.jhernandez.frontend.bazaar.data.network;

import static com.jhernandez.frontend.bazaar.core.constants.Values.CATEGORIES_ENABLED;
import static com.jhernandez.frontend.bazaar.core.constants.Values.CATEGORIES_RANDOM;
import static com.jhernandez.frontend.bazaar.core.constants.Values.CATEGORY_ID;
import static com.jhernandez.frontend.bazaar.core.constants.Values.HEADER_AUTHORIZATION;
import static com.jhernandez.frontend.bazaar.core.constants.Values.IMAGE_ID;
import static com.jhernandez.frontend.bazaar.core.constants.Values.LOGIN;
import static com.jhernandez.frontend.bazaar.core.constants.Values.METHOD_GET;
import static com.jhernandez.frontend.bazaar.core.constants.Values.METHOD_POST;
import static com.jhernandez.frontend.bazaar.core.constants.Values.PING;
import static com.jhernandez.frontend.bazaar.core.constants.Values.PREFIX_TOKEN;
import static com.jhernandez.frontend.bazaar.core.constants.Values.PRODUCTS_CATEGORY_ID;
import static com.jhernandez.frontend.bazaar.core.constants.Values.PRODUCTS_ENABLED;
import static com.jhernandez.frontend.bazaar.core.constants.Values.PRODUCTS_RECENT;
import static com.jhernandez.frontend.bazaar.core.constants.Values.PRODUCTS_SEARCH;
import static com.jhernandez.frontend.bazaar.core.constants.Values.PRODUCTS_TOP_RATED;
import static com.jhernandez.frontend.bazaar.core.constants.Values.PRODUCTS_TOP_SELLING;
import static com.jhernandez.frontend.bazaar.core.constants.Values.PRODUCTS_USER_ID;
import static com.jhernandez.frontend.bazaar.core.constants.Values.PRODUCT_ID;
import static com.jhernandez.frontend.bazaar.core.constants.Values.REGISTER;
import static com.jhernandez.frontend.bazaar.core.constants.Values.REVIEW_PRODUCT_ID;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ROLES;
import static com.jhernandez.frontend.bazaar.core.constants.Values.USER_ID;

import java.io.IOException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Interceptor class for adding authorization headers to requests.
 */
@RequiredArgsConstructor
public class AuthInterceptor implements Interceptor {

    private final SessionManager sessionManager;

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Do not attach token to public endpoints requests
        if (isPublicRequest(originalRequest)) {
            return chain.proceed(originalRequest);
        }

        return (sessionManager.hasToken())
                ? chain.proceed(originalRequest.newBuilder()
                    .addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + sessionManager.getToken())
                    .build())
                : chain.proceed(originalRequest);
    }

    private boolean isPublicRequest(Request request) {
        String path = request.url().encodedPath();

        return switch (request.method()) {
            case METHOD_GET -> path.equals(PING) ||
                    path.equals(USER_ID) ||
                    path.equals(ROLES) ||
                    path.equals(CATEGORIES_ENABLED) ||
                    path.equals(CATEGORY_ID) ||
                    path.equals(CATEGORIES_RANDOM) ||
                    path.equals(PRODUCTS_ENABLED) ||
                    path.equals(PRODUCT_ID) ||
                    path.equals(PRODUCTS_USER_ID) ||
                    path.equals(PRODUCTS_CATEGORY_ID) ||
                    path.equals(PRODUCTS_SEARCH) ||
                    path.equals(PRODUCTS_RECENT) ||
                    path.equals(PRODUCTS_TOP_SELLING) ||
                    path.equals(PRODUCTS_TOP_RATED) ||
                    path.equals(IMAGE_ID) ||
                    path.equals(REVIEW_PRODUCT_ID);
            case METHOD_POST -> path.equals(REGISTER) ||
                    path.equals(LOGIN);
            default -> false;
        };
    }
}
