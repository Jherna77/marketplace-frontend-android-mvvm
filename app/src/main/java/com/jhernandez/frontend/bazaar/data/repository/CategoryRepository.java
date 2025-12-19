package com.jhernandez.frontend.bazaar.data.repository;

import com.jhernandez.frontend.bazaar.data.api.ApiService;
import com.jhernandez.frontend.bazaar.data.mapper.CategoryMapper;
import com.jhernandez.frontend.bazaar.data.network.CallbackDelegator;
import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Category;
import com.jhernandez.frontend.bazaar.domain.port.CategoryRepositoryPort;
import com.jhernandez.frontend.bazaar.data.network.JsonUtils;

import java.io.File;
import java.util.List;

import lombok.RequiredArgsConstructor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Repository class for managing category-related operations.
 */
@RequiredArgsConstructor
public class CategoryRepository implements CategoryRepositoryPort {

    private final ApiService apiService;

    @Override
    public void createCategory(Category category, File imageFile, SuccessCallback callback) {
        RequestBody categoryBody = JsonUtils.createJsonBody(CategoryMapper.toDto(category));
        MultipartBody.Part imagePart = JsonUtils.createImagePart(imageFile);
        apiService.createCategory(categoryBody, imagePart)
                .enqueue(CallbackDelegator.delegate("createCategory", callback));
    }

    @Override
    public void findAllCategories(TypeCallback<List<Category>> callback) {
        apiService.findAllCategories()
                .enqueue(CallbackDelegator.delegate(
                        "findAllCategories",
                        response ->
                                callback.onSuccess(CategoryMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void findAllEnabledCategories(TypeCallback<List<Category>> callback) {
        apiService.findAllEnabledCategories()
                .enqueue(CallbackDelegator.delegate(
                        "findAllEnabledCategories",
                        response ->
                                callback.onSuccess(CategoryMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void findRandomCategories(TypeCallback<List<Category>> callback) {
        apiService.findRandomCategories()
                .enqueue(CallbackDelegator.delegate(
                        "findRandomCategories",
                        response ->
                                callback.onSuccess(CategoryMapper.toDomainList(response)),
                                 callback::onError));
    }

    @Override
    public void findCategoryById(Long id, TypeCallback<Category> callback) {
        apiService.findCategoryById(id)
                .enqueue(CallbackDelegator.delegate(
                        "findCategoryById",
                        response ->
                                callback.onSuccess(CategoryMapper.toDomain(response)),
                                callback::onError));

    }

    @Override
    public void updateCategory(Category category, File imageFile, SuccessCallback callback) {
        RequestBody categoryBody = JsonUtils.createJsonBody(CategoryMapper.toDto(category));
        MultipartBody.Part imagePart = JsonUtils.createImagePart(imageFile);
        apiService.updateCategory(category.id(), categoryBody, imagePart)
                .enqueue(CallbackDelegator.delegate("updateCategory", callback));
    }

    @Override
    public void enableCategoryById(Long id, SuccessCallback callback) {
        apiService.enableCategory(id)
                .enqueue(CallbackDelegator.delegate("enableCategory", callback));
    }

    @Override
    public void disableCategoryById(Long id, SuccessCallback callback) {
        apiService.disableCategory(id)
                .enqueue(CallbackDelegator.delegate("disableCategory", callback));
    }

}
