package com.jhernandez.frontend.bazaar.data.api;

import static com.jhernandez.frontend.bazaar.core.constants.Values.*;

import com.jhernandez.frontend.bazaar.data.model.BackupDto;
import com.jhernandez.frontend.bazaar.data.model.CategoryDto;
import com.jhernandez.frontend.bazaar.data.model.ItemDto;
import com.jhernandez.frontend.bazaar.data.model.LoginRequestDto;
import com.jhernandez.frontend.bazaar.data.model.LoginResponseDto;
import com.jhernandez.frontend.bazaar.data.model.MessageDto;
import com.jhernandez.frontend.bazaar.data.model.OrderDto;
import com.jhernandez.frontend.bazaar.data.model.OrderStatusDto;
import com.jhernandez.frontend.bazaar.data.model.PaymentRequestDto;
import com.jhernandez.frontend.bazaar.data.model.PaymentResponseDto;
import com.jhernandez.frontend.bazaar.data.model.ProductDto;
import com.jhernandez.frontend.bazaar.data.model.ReviewDto;
import com.jhernandez.frontend.bazaar.data.model.UserRequestDto;
import com.jhernandez.frontend.bazaar.data.model.UserResponseDto;
import com.jhernandez.frontend.bazaar.data.model.UserRoleDto;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/* 
 * Retrofit API service interface defining all endpoints for the Bazaar application.
 * Public endpoints:
 *      - GET /ping
 *      - POST /login
 *      - POST /api/users/register
 *      - GET /api/users/{id}
 *      - GET /api/roles
 *      - GET /api/categories/enabled
 *      - GET /api/categories/{id}
 *      - GET /api/categories/random
 *      - GET /api/products/enabled
 *      - GET /api/products/{id}
 *      - GET /api/products/user/{userId}
 *      - GET /api/products/category/{categoryId}
 *      - GET /api/products/search/{name}
 *      - GET /api/products/recent
 *      - GET /api/products/top-rated
 *      - GET /api/products/top-selling
 *      - GET /api/images/{filename:.+}
 *      - GET /api/reviews/product/{productId}
 *
 * All other endpoints require authentication.
 */

public interface ApiService {

    // ----------- PUBLIC ENDPOINTS (No Auth Required) -----------

    @GET(PING)
    Call<Void> testConnection();

    @POST(LOGIN)
    Call<LoginResponseDto> login(@Body LoginRequestDto loginRequest);

    @POST(REGISTER)
    Call<Void> createUser(@Body UserRequestDto user);

    @GET(USER_ID)
    Call<UserResponseDto> findUserById(@Path(ARG_ID) Long id);

    @GET(ROLES)
    Call<List<UserRoleDto>> findAllUserRoles();

    @GET(CATEGORIES_ENABLED)
    Call<List<CategoryDto>> findAllEnabledCategories();

    @GET(CATEGORY_ID)
    Call<CategoryDto> findCategoryById(@Path(ARG_ID) Long id);

    @GET(CATEGORIES_RANDOM)
    Call<List<CategoryDto>> findRandomCategories();

    @GET(PRODUCT_ID)
    Call<ProductDto> findProductById(@Path(ARG_ID) Long id);

    @GET(PRODUCTS_USER_ID)
    Call<List<ProductDto>> findProductsByUserId(@Path("userId") Long userId);

    @GET(PRODUCTS_CATEGORY_ID)
    Call<List<ProductDto>> findProductsByCategoryId(@Path("categoryId") Long categoryId);

    @GET(PRODUCTS_SEARCH)
    Call<List<ProductDto>> findProductsByName(@Path("name") String name);

    @GET(PRODUCTS_RECENT)
    Call<List<ProductDto>> findRecentProducts();

    @GET(PRODUCTS_TOP_SELLING)
    Call<List<ProductDto>> findTopSellingProducts();

    @GET(PRODUCTS_TOP_RATED)
    Call<List<ProductDto>> findTopRatedProducts();

    @GET(PRODUCTS_DISCOUNTED)
    Call<List<ProductDto>> findDiscountedProducts();

    @GET(REVIEW_PRODUCT_ID)
    Call<List<ReviewDto>> findReviewsByProductId(@Path("productId") Long productId);

    // ----------- PRIVATE ENDPOINTS (Require Auth Token) -----------

    @GET(VALIDATE_TOKEN)
    Call<Void> validateToken();

    @GET(USERS)
    Call<List<UserResponseDto>> findAllUsers();

    @GET(USER_EMAIL)
    Call<UserResponseDto> findUserByEmail(@Path(ARG_EMAIL) String email);

    @PUT(USER_ID)
    Call<Void> updateUser(@Path(ARG_ID) Long id, @Body UserRequestDto user);

    @PUT(USER_ENABLE_ID)
    Call<Void> enableUser(@Path(ARG_ID) Long id);

    @PUT(USER_DISABLE_ID)
    Call<Void> disableUser(@Path(ARG_ID) Long id);

    @Multipart
    @POST(CATEGORIES)
    Call<Void> createCategory(
            @Part(ARG_CATEGORY) RequestBody category,
            @Part MultipartBody.Part image
    );

    @GET(CATEGORIES)
    Call<List<CategoryDto>> findAllCategories();

    @Multipart
    @PUT(CATEGORY_ID)
    Call<Void> updateCategory(
            @Path(ARG_ID) Long id,
            @Part(ARG_CATEGORY) RequestBody category,
            @Part MultipartBody.Part image
    );

    @PUT(CATEGORY_ENABLE_ID)
    Call<Void> enableCategory(@Path(ARG_ID) Long id);

    @PUT(CATEGORY_DISABLE_ID)
    Call<Void> disableCategory(@Path(ARG_ID) Long id);

    @Multipart
    @POST(PRODUCTS)
    Call<Void> createProduct(
            @Part(ARG_PRODUCT) RequestBody product,
            @Part List<MultipartBody.Part> images
    );

    @GET(PRODUCTS)
    Call<List<ProductDto>> findAllProducts();

    @Multipart
    @PUT(PRODUCT_ID)
    Call<Void> updateProduct(
            @Path(ARG_ID) Long id,
            @Part(ARG_PRODUCT) RequestBody product,
            @Part List<MultipartBody.Part> images
    );

    @PUT(PRODUCT_ENABLE_ID)
    Call<Void> enableProduct(@Path(ARG_ID) Long id);

    @PUT(PRODUCT_DISABLE_ID)
    Call<Void> disableProduct(@Path(ARG_ID) Long id);

    @GET(CART)
    Call<List<ItemDto>> loadUserCart(@Path(ARG_ID) Long id);

    @POST(CART)
    Call<Void> addItemToUserCart(@Path(ARG_ID) Long id, @Body ItemDto item);

    @DELETE(CART_ITEM_ID)
    Call<List<ItemDto>> removeItemFromUserCart(@Path(ARG_ID) Long id, @Path("itemId") Long itemId);

    @PUT(CART_ITEM_QUANTITY)
    Call<List<ItemDto>> updateItemQuantity(@Path(ARG_ID) Long id, @Path("itemId") Long itemId, @Path("quantity") Integer quantity);

    @PUT(CART_ID_CLEAR)
    Call<List<ItemDto>> clearUserCart(@Path(ARG_ID) Long userId);

    @POST(ORDER_USER_ID)
    Call<Void> createOrderFromCart(@Path("userId") Long UserId);

    @GET(ORDER_PURCHASE_ID)
    Call<List<OrderDto>> findPurchaseOrdersByUserId(@Path("userId") Long userId);

    @GET(ORDER_SALE_ID)
    Call<List<OrderDto>> findSaleOrdersByUserId(@Path("userId") Long userId);

    @GET(ORDER_ID)
    Call<OrderDto> findOrderById(@Path(ARG_ID) Long id);

    @PUT(ORDER_ID)
    Call<OrderDto> updateOrderStatus(@Path(ARG_ID) Long id, @Body OrderStatusDto statusDto);

    @GET(STATUSES)
    Call<String[]> findAllOrderStatuses();

    @POST(REVIEWS)
    Call<Void> createReview(@Body ReviewDto review);

    @GET(REVIEW_ID)
    Call<ReviewDto> findReviewById(@Path(ARG_ID) Long id);

    @GET(REVIEW_USER_ID)
    Call<List<ReviewDto>> findReviewsByUserId(@Path("userId") Long userId);

    @GET(PREFERENCES_PRODUCTS)
    Call<List<ProductDto>> findUserFavouriteProducts(@Path(ARG_ID) Long id);

    @GET(PREFERENCES_PRODUCTS_ID)
    Call<Boolean> isFavouriteProduct(@Path(ARG_ID) Long id, @Path("productId") Long productId);

    @PUT(PREFERENCES_PRODUCTS_ID)
    Call<Void> addProductToFavourites(@Path(ARG_ID) Long id, @Path("productId") Long productId);

    @DELETE(PREFERENCES_PRODUCTS_ID)
    Call<Void> removeProductFromFavourites(@Path(ARG_ID) Long id, @Path("productId") Long productId);

    @GET(PREFERENCES_CATEGORIES)
    Call<List<CategoryDto>> findUserFavouriteCategories(@Path(ARG_ID) Long id);

    @POST(PAYMENTS)
    Call<PaymentResponseDto> createPaymentIntent(@Body PaymentRequestDto paymentRequest);

    @POST(BACKUPS)
    Call<Void> createBackup();

    @GET(BACKUPS)
    Call<List<BackupDto>> findAllBackups();

    @GET(BACKUP_ID)
    Call<BackupDto> findBackupById(@Path(ARG_ID) Long id);

    @PUT(BACKUP_RESTORE)
    Call<Void> restoreBackup(@Path(ARG_ID) Long id);

    @GET(MESSAGE_ID)
    Call<MessageDto> findMessageById(@Path(ARG_ID) Long id);

    @GET(MESSAGE_RECIPIENT_ID)
    Call<List<MessageDto>> findMessagesByRecipientId(@Path("recipientId") Long recipientId);

    @GET(MESSAGE_RECIPIENT_ID_NEW)
    Call<Boolean> hasNewMessages(@Path("recipientId") Long recipientId);

    @PUT(MESSAGE_ID)
    Call<Void> setMessageAsSeen(@Path(ARG_ID) Long id);

    @DELETE(MESSAGE_ID)
    Call<List<MessageDto>> deleteMessageById(@Path(ARG_ID) Long id);

}
