package com.jhernandez.frontend.bazaar.ui.category.manage;

/*
 * ViewModel for managing (adding/editing) a category.
 * Handles user interactions for category details, image selection, and saving changes.
 */
public record ManageCategoryViewState(Boolean isUpdate, Boolean isLoading, Boolean isEnabled,
                                      Boolean hasImage) {

    public ManageCategoryViewState withLoading(Boolean loading) {
        return new ManageCategoryViewState(isUpdate, loading, isEnabled, hasImage);
    }

    public ManageCategoryViewState withEnabled(Boolean enabled) {
        return new ManageCategoryViewState(isUpdate, isLoading, enabled, hasImage);
    }

    public ManageCategoryViewState withImage(Boolean image) {
        return new ManageCategoryViewState(isUpdate, isLoading, isEnabled, image);
    }
}
