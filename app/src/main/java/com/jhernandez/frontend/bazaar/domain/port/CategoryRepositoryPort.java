package com.jhernandez.frontend.bazaar.domain.port;

import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Category;

import java.io.File;
import java.util.List;

/*
 * Interface representing the CategoryRepositoryPort.
 */
public interface CategoryRepositoryPort {

    void createCategory(Category category, File imageFile, SuccessCallback callback);
    void findAllCategories(TypeCallback<List<Category>> callback);
    void findAllEnabledCategories(TypeCallback<List<Category>> callback);
    void findRandomCategories(TypeCallback<List<Category>> callback);
    void findCategoryById(Long id, TypeCallback<Category> callback);
    void updateCategory(Category category, File imageFile, SuccessCallback callback);
    void enableCategoryById(Long id, SuccessCallback callback);
    void disableCategoryById(Long id, SuccessCallback callback);

}
