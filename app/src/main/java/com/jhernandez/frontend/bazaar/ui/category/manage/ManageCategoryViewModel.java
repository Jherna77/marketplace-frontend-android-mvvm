package com.jhernandez.frontend.bazaar.ui.category.manage;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_CATEGORY;
import static com.jhernandez.frontend.bazaar.core.constants.Values.NO_ARG;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.core.file.FileProviderService;
import com.jhernandez.frontend.bazaar.domain.error.ValidationError;
import com.jhernandez.frontend.bazaar.domain.model.Category;
import com.jhernandez.frontend.bazaar.domain.port.CategoryRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

import java.io.File;
import java.util.Objects;

/*
 * ViewModel for managing (adding/editing) a category.
 * Handles user interactions for category details, image selection, and saving changes.
 */
public class ManageCategoryViewModel extends BaseViewModel {

    private final CategoryRepositoryPort categoryRepository;
    private final FileProviderService fileProvider;
    private final MutableLiveData<ManageCategoryViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<Category> category = new MutableLiveData<>();
    private final MutableLiveData<Uri> categoryImage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _addImage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _removeImage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _enableCategory = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _disableCategory = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goBack = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cancelledAction = new MutableLiveData<>();
    private final MutableLiveData<ValidationError> validationError = new MutableLiveData<>();

    public ManageCategoryViewModel(CategoryRepositoryPort categoryRepository, FileProviderService fileProvider) {
        this.categoryRepository = categoryRepository;
        this.fileProvider = fileProvider;
    }

    public LiveData<ManageCategoryViewState> getViewState() {
        return viewState;
    }
    public LiveData<Category> getCategory() {
        return category;
    }
    public LiveData<Uri> getCategoryImage() {
        return categoryImage;
    }
    public LiveData<Boolean> addImageEvent() {
        return _addImage;
    }
    public LiveData<Boolean> removeImageEvent() {
        return _removeImage;
    }
    public LiveData<Boolean> enableCategoryEvent() {
        return _enableCategory;
    }
    public LiveData<Boolean> disableCategoryEvent() {
        return _disableCategory;
    }
    public LiveData<Boolean> goBackEvent() {
        return _goBack;
    }
    public LiveData<Boolean> isCancelledAction() {
        return cancelledAction;
    }
    public LiveData<ValidationError> getValidationError() {
        return validationError;
    }

    public void setViewState(Long categoryId) {
        Boolean isUpdate = !Objects.equals(categoryId, NO_ARG);
        viewState.setValue(new ManageCategoryViewState(
                isUpdate,
                false,
                true,
                false
        ));

        if (isUpdate) { findCategoryById(categoryId); }
    }

    private void showLoading(Boolean isLoading) {
        viewState.setValue(viewState.getValue().withLoading(isLoading));
    }

    private void updateEnabled(boolean enabled) {
        viewState.setValue(viewState.getValue().withEnabled(enabled));
    }

    private void updateImage(Boolean hasImage) {
        viewState.setValue(viewState.getValue().withImage(hasImage));
    }

    public void createCategory(Category category, File imageFile) {
        showLoading(true);
        Log.d("ManageCategoryViewModel", "Creating category " + category.name());
        categoryRepository.createCategory(category, imageFile, successCallback(
                () -> {
                    _goBack.postValue(true);
                    showLoading(false);
                },
                error -> showLoading(false)
        ));
    }

    public void findCategoryById(Long id) {
        showLoading(true);
        Log.d("ManageCategoryViewModel", "Loading category with id " + id);
        categoryRepository.findCategoryById(id, typeCallback(
                category,
                categoryResult -> {
                    updateEnabled(categoryResult.enabled());
                    updateImage(categoryResult.imageUrl() != null);
                    showLoading(false);
                },
                error -> showLoading(false)
        ));
    }

    public void updateCategory(Category category, File imageFile) {
        showLoading(true);
        Log.d("ManageCategoryViewModel", "Updating category " + category.name());
        categoryRepository.updateCategory(category, imageFile, successCallback(
                () -> {
                    _goBack.postValue(true);
                    showLoading(false);
                },
                error -> showLoading(false)
        ));
    }

    public void enableCategory(Long id) {
        showLoading(true);
        Log.d("ManageCategoryViewModel", "Enabling category with id " + id);
        categoryRepository.enableCategoryById(id, successCallback(
                () -> {
                    _goBack.postValue(true);
                    showLoading(false);
                },
                error -> showLoading(false)
        ));
    }

    public void disableCategory(Long id) {
        showLoading(true);
        Log.d("ManageCategoryViewModel", "Disabling category with id " + id);
        categoryRepository.disableCategoryById(id, successCallback(
                () -> {
                    _goBack.postValue(true);
                    showLoading(false);
                },
                error -> showLoading(false)
        ));
    }

    public void validateFields(String name) {
        if (name.isEmpty()) { validationError.setValue(ValidationError.FIELD_EMPTY); }
        else if (categoryImage.getValue() == null &&
                (category.getValue() == null || category.getValue().imageUrl() == null)) {
            validationError.setValue(ValidationError.NO_IMAGES); }
        else {
            validationError.setValue(null);
            submitCategory(name);
        }
    }

    private void submitCategory(String name) {
        File imageFile = fileProvider.getFileFromUri(categoryImage.getValue(), ARG_CATEGORY);
        if (viewState.getValue().isUpdate()) {
            Category existing = category.getValue();
            updateCategory(new Category(existing.id(), existing.enabled(), name, existing.imageUrl()), imageFile);
        } else {
            createCategory(new Category(null, true, name, null), imageFile);
        }
    }

    public void onImageSelected(Uri uri) {
        categoryImage.setValue(uri);
        updateImage(true);
    }

    public void onAddImageSelected() {
        _addImage.setValue(true);
    }

    public void onRemoveImageSelected() {
        _removeImage.setValue(true);
    }

    public void onRemoveImageConfirmation(Boolean isConfirmed) {
        if (isConfirmed) {
            categoryImage.setValue(null);
            updateImage(false);
        }
        else { onCancelActionSelected(); }
    }

    public void onEnableCategorySelected() {
        _enableCategory.setValue(true);
    }

    public void onEnableCategoryConfirmation(Boolean isConfirmed) {
        if (isConfirmed) { enableCategory(category.getValue().id()); }
        else { onCancelActionSelected(); }
    }

    public void onDisableCategorySelected() {
        _disableCategory.setValue(true);
    }

    public void onDisableCategoryConfirmation(Boolean isConfirmed) {
        if (isConfirmed) { disableCategory(category.getValue().id()); }
        else { onCancelActionSelected(); }
    }

    public void onCancelActionSelected() {
        cancelledAction.setValue(true);
    }

    public void onGoBackSelected() {
        _goBack.setValue(true);
    }

}
