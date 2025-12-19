package com.jhernandez.frontend.bazaar.core.constants;

/*
 * Class that contains all the constant values used in the application.
 */
public class Values {

    // API constants
    public static final String BASE_URL = "http://your.domain.com";
    public static final String PREFIX_TOKEN = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";

    // Arguments
    public static final String ARG_USER = "user";
    public static final String ARG_ID = "id";
    public static final String ARG_CATEGORY = "category";
    public static final String ARG_PRODUCT = "product";
    public static final String ARG_IMAGE = "image";
    public static final String ARG_EMAIL = "email";
    public static final String ARG_FRAGMENT = "fragment";
    public static final String ARG_HOME = "home";
    public static final String ARG_SEARCH = "search";
    public static final String ARG_CART = "cart";
    public static final String ARG_PROFILE = "profile";
    public static final String ARG_MESSAGE = "message";
    public static final String ARG_ORDER = "order";
    public static final String ARG_REVIEW = "review";
    public static final String ARG_BACKUP = "backup";
    public static final Long NO_ARG = 0L;

    // Tags
    public static final String TAG_ALL = "ALL";
    public static final String TAG_ENABLED = "ENABLED";
    public static final String TAG_DISABLED = "DISABLED";
    public static final String TAG_ASCENDING = "ASC";
    public static final String TAG_DESCENDING = "DESC";
    public static final String TAG_PRICE_ASCENDING = "PRICE_ASC";
    public static final String TAG_PRICE_DESCENDING = "PRICE_DESC";
    public static final String TAG_RATING_ASCENDING = "RATING_ASC";
    public static final String TAG_RATING_DESCENDING = "RATING_DESC";
    public static final String TAG_PROMO = "PROMO";

    // UserRoles
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String SHOP = "ROLE_SHOP";
    public static final String CUSTOMER = "ROLE_CUSTOMER";

    // OrderStatuses
    public static final String PENDING = "PENDING";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String SHIPPED = "SHIPPED";
    public static final String DELIVERED = "DELIVERED";
    public static final String CANCELLED = "CANCELLED";
    public static final String RETURNED = "RETURNED";

    // HTTP methods
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    // API endpoints
    public static final String VALIDATE_TOKEN = "/validate-token";
    public static final String PING = "/ping";
    public static final String LOGIN = "/login";

    public static final String USERS = "/api/users";
    public static final String REGISTER = USERS + "/register";
    public static final String USER_ENABLE_ID = USERS + "/enable/{id}";
    public static final String USER_DISABLE_ID = USERS + "/disable/{id}";
    public static final String USER_ID = USERS + "/{id}";
    public static final String USER_EMAIL = USERS + "/email/{email}";

    public static final String ROLES = "/api/roles";

    public static final String CATEGORIES = "/api/categories";
    public static final String CATEGORIES_RANDOM = CATEGORIES + "/random";
    public static final String CATEGORIES_ENABLED = CATEGORIES + "/enabled";
    public static final String CATEGORY_ENABLE_ID = CATEGORIES + "/enable/{id}";
    public static final String CATEGORY_DISABLE_ID = CATEGORIES + "/disable/{id}";
    public static final String CATEGORY_ID = CATEGORIES + "/{id}";

    public static final String PRODUCTS = "/api/products";
    public static final String PRODUCTS_ENABLED = PRODUCTS + "/enabled";
    public static final String PRODUCT_ENABLE_ID = PRODUCTS + "/enable/{id}";
    public static final String PRODUCT_DISABLE_ID = PRODUCTS + "/disable/{id}";
    public static final String PRODUCT_ID = PRODUCTS + "/{id}";
    public static final String PRODUCTS_USER_ID = PRODUCTS + "/user/{userId}";
    public static final String PRODUCTS_CATEGORY_ID = PRODUCTS + "/category/{categoryId}";
    public static final String PRODUCTS_SEARCH = PRODUCTS + "/search/{name}";
    public static final String PRODUCTS_RECENT = PRODUCTS + "/recent";
    public static final String PRODUCTS_TOP_SELLING = PRODUCTS + "/top-selling";
    public static final String PRODUCTS_TOP_RATED = PRODUCTS + "/top-rated";
    public static final String PRODUCTS_DISCOUNTED = PRODUCTS + "/discounted";

    public static final String IMAGES = "/api/images";
    public static final String IMAGES_UPLOAD = IMAGES + "/upload";
    public static final String IMAGE_ID = IMAGES + "/{filename:.+}";

    public static final String ORDERS = "/api/orders";
    public static final String ORDER_ID = ORDERS + "/{id}";
    public static final String ORDER_USER_ID = ORDERS + "/user/{userId}";
    public static final String ORDER_PURCHASE_ID = ORDERS + "/purchase/{userId}";
    public static final String ORDER_SALE_ID = ORDERS + "/sale/{userId}";

    public static final String CART = USER_ID + "/cart";
    public static final String CART_ITEM_ID = CART + "/{itemId}";
    public static final String CART_ITEM_QUANTITY = CART_ITEM_ID + "/{quantity}";
    public static final String CART_ID_CLEAR = CART + "/clear";

    public static final String STATUSES="/api/statuses";

    public static final String PAYMENTS = "/api/payments";

    public static final String REVIEWS = "/api/reviews";
    public static final String REVIEW_ID = REVIEWS + "/{id}";
    public static final String REVIEW_PRODUCT_ID = REVIEWS + "/product/{productId}";
    public static final String REVIEW_USER_ID = REVIEWS + "/user/{userId}";

    public static final String PREFERENCES = USER_ID + "/preferences";
    public static final String PREFERENCES_PRODUCTS = PREFERENCES + "/products";
    public static final String PREFERENCES_PRODUCTS_ID = PREFERENCES_PRODUCTS + "/{productId}";
    public static final String PREFERENCES_CATEGORIES = PREFERENCES + "/categories";

    public static final String BACKUPS = "/api/backups";
    public static final String BACKUP_ID = BACKUPS + "/{id}";
    public static final String BACKUP_RESTORE = BACKUPS + "/restore/{id}";

    public static final String MESSAGES = "/api/messages";
    public static final String MESSAGE_ID = MESSAGES + "/{id}";
    public static final String MESSAGE_RECIPIENT_ID = MESSAGES + "/recipient/{recipientId}";
    public static final String MESSAGE_RECIPIENT_ID_NEW = MESSAGE_RECIPIENT_ID + "/new";

}
