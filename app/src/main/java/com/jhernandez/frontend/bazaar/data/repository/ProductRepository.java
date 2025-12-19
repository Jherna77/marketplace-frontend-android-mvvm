package com.jhernandez.frontend.bazaar.data.repository;

import com.jhernandez.frontend.bazaar.data.api.ApiService;
import com.jhernandez.frontend.bazaar.data.mapper.ProductMapper;
import com.jhernandez.frontend.bazaar.data.network.CallbackDelegator;
import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.domain.port.ProductRepositoryPort;
import com.jhernandez.frontend.bazaar.data.network.JsonUtils;

import java.io.File;
import java.util.List;

import lombok.RequiredArgsConstructor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Repository class for managing product-related operations.
 */
@RequiredArgsConstructor
public class ProductRepository implements ProductRepositoryPort {

    private final ApiService apiService;

    @Override
    public void createProduct(Product product, List<File> imageFiles, SuccessCallback callback) {
        RequestBody productBody = JsonUtils.createJsonBody(ProductMapper.toDto(product));
        List<MultipartBody.Part> imagePartList = JsonUtils.createImagePartList(imageFiles);
        apiService.createProduct(productBody, imagePartList)
                .enqueue(CallbackDelegator.delegate("createProduct", callback));
    }

    @Override
    public void findAllProducts(TypeCallback<List<Product>> callback) {
        apiService.findAllProducts()
                .enqueue(CallbackDelegator.delegate(
                        "findAllProducts",
                        response ->
                                callback.onSuccess(ProductMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void findProductById(Long id, TypeCallback<Product> callback) {
        apiService.findProductById(id)
                .enqueue(CallbackDelegator.delegate(
                        "findProductById",
                        response ->
                                callback.onSuccess(ProductMapper.toDomain(response)),
                                callback::onError));
    }

    public void findProductsByUserId(Long userId, TypeCallback<List<Product>> callback) {
        apiService.findProductsByUserId(userId)
                .enqueue(CallbackDelegator.delegate(
                        "findProductsByUserId",
                        response ->
                                callback.onSuccess(ProductMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void findProductsByCategoryId(Long categoryId, TypeCallback<List<Product>> callback) {
        apiService.findProductsByCategoryId(categoryId)
                .enqueue(CallbackDelegator.delegate(
                        "findProductsByCategoryId",
                        response ->
                                callback.onSuccess(ProductMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void findProductsByName(String name, TypeCallback<List<Product>> callback) {
        apiService.findProductsByName(name)
                .enqueue(CallbackDelegator.delegate(
                        "findProductsByName",
                        response ->
                                callback.onSuccess(ProductMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void findRecentProducts(TypeCallback<List<Product>> callback) {
        apiService.findRecentProducts()
                .enqueue(CallbackDelegator.delegate(
                        "findRecentProducts",
                        response ->
                                callback.onSuccess(ProductMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void findTopSellingProducts(TypeCallback<List<Product>> callback) {
        apiService.findTopSellingProducts()
                .enqueue(CallbackDelegator.delegate(
                        "findTopSellingProducts",
                        response ->
                                callback.onSuccess(ProductMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void findTopRatedProducts(TypeCallback<List<Product>> callback) {
        apiService.findTopRatedProducts()
                .enqueue(CallbackDelegator.delegate(
                        "findTopRatedProducts",
                        response ->
                                callback.onSuccess(ProductMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void findDiscountedProducts(TypeCallback<List<Product>> callback) {
        apiService.findDiscountedProducts()
                .enqueue(CallbackDelegator.delegate(
                        "findDiscountedProducts",
                        response ->
                                callback.onSuccess(ProductMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void updateProduct(Product product, List<File> imageFiles, SuccessCallback callback) {
        RequestBody productBody = JsonUtils.createJsonBody(ProductMapper.toDto(product));
        List<MultipartBody.Part> imagePartList = JsonUtils.createImagePartList(imageFiles);
        apiService.updateProduct(product.id(), productBody, imagePartList)
                .enqueue(CallbackDelegator.delegate("updateProduct", callback));
    }

    @Override
    public void enableProductById(Long id, SuccessCallback callback) {
        apiService.enableProduct(id)
                .enqueue(CallbackDelegator.delegate("enableProduct", callback));
    }

    @Override
    public void disableProductById(Long id, SuccessCallback callback) {
        apiService.disableProduct(id)
                .enqueue(CallbackDelegator.delegate("disableProduct", callback));
    }

}
