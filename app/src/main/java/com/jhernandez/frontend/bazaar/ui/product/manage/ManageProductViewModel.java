package com.jhernandez.frontend.bazaar.ui.product.manage;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_PRODUCT;
import static com.jhernandez.frontend.bazaar.core.constants.Values.BASE_URL;
import static com.jhernandez.frontend.bazaar.core.constants.Values.NO_ARG;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.core.file.FileProviderService;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.error.ValidationError;
import com.jhernandez.frontend.bazaar.domain.model.Category;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.port.CategoryRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.ProductRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/*
 * ViewModel for managing a product.
 * It handles creating, updating, and deleting products, as well as image selection and validation.
 */
public class ManageProductViewModel extends BaseViewModel {

    private static final Integer MAX_PRODUCT_IMAGES = 4;

    private final ProductRepositoryPort productRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final FileProviderService fileProvider;
    private final User shop;
    private final MutableLiveData<ManageProductViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<String> _updateCategoriesInfo = new MutableLiveData<>();
    private final MutableLiveData<List<Uri>> productImages = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _launchImagePicker = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _maxImagesReached = new MutableLiveData<>();
    private final MutableLiveData<Uri> newImage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _imageRemoved = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _enableProduct = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _disableProduct = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goBack = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cancelledAction = new MutableLiveData<>();
    private final MutableLiveData<ValidationError> validationError = new MutableLiveData<>();
    private final MutableLiveData<ViewUtils.SelectionDialogData<Category>> categoryDialogData = new MutableLiveData<>();
    private List<Category> allCategories;
    private List<Category> productCategories;

    public ManageProductViewModel(ProductRepositoryPort productRepository, CategoryRepositoryPort categoryRepository,
                                  SessionRepositoryPort sessionRepository, FileProviderService fileProvider) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.shop = sessionRepository.getSessionUser();
        this.fileProvider = fileProvider;
    }

    public LiveData<ManageProductViewState> getViewState() {
        return viewState;
    }
    public LiveData<Product> getProduct() {
        return product;
    }
    public LiveData<List<Uri>> getProductImages() {
        return productImages;
    }
    public LiveData<Boolean> launchImagePickerEvent() {
        return _launchImagePicker;
    }
    public LiveData<Boolean> maxImagesReachedEvent() {
        return _maxImagesReached;
    }
    public LiveData<Uri> getNewImage() {
        return newImage;
    }
    public LiveData<Boolean> isImageRemoved() {
        return _imageRemoved;
    }
    public LiveData<String> updateCategoriesInfoEvent() {
        return _updateCategoriesInfo;
    }
    public LiveData<Boolean> enableProductEvent() {
        return _enableProduct;
    }
    public LiveData<Boolean> disableProductEvent() {
        return _disableProduct;
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
    public LiveData<ViewUtils.SelectionDialogData<Category>> getCategoryDialogData() {
        return categoryDialogData;
    }

    public void setViewState(Long productId) {
        Boolean isUpdate = !Objects.equals(productId, NO_ARG);
        viewState.setValue(new ManageProductViewState(
                isUpdate,
                false,
                true,
                false
        ));

        if (isUpdate) { findProductById(productId); }
        else {
            productCategories = new ArrayList<>();
            productImages.setValue(new ArrayList<>());
        }
    }

    private void showLoading(Boolean isLoading) {
        viewState.setValue(viewState.getValue().withLoading(isLoading));
    }

    private void updateEnabled(Boolean enabled) {
        viewState.setValue(viewState.getValue().withEnabled(enabled));
    }

    private void updateDiscount(Boolean discount) {
        viewState.setValue(viewState.getValue().withDiscount(discount));
    }

    // Create product on the server
    private void createProduct(Product product, List<File> imageFiles) {
        showLoading(true);
        Log.d("ManageProductViewModel", "Creating product " + product.name());
        productRepository.createProduct(product, imageFiles, successCallback(
                () -> {
                    _goBack.postValue(true);
                    showLoading(false);
                },
                error -> showLoading(false)
        ));
    }

    // Load product from the server
    public void findProductById(Long id) {
        showLoading(true);
        Log.d("ManageProductViewModel", "Loading product with id " + id);
        productRepository.findProductById(id, typeCallback(
                product,
                productResult -> {
                    productImages.postValue(fileProvider.getUrisFromUrlList(productResult.imagesUrl()));
                    productCategories = productResult.categories();
                    setCategoriesInfo();
                    updateEnabled(productResult.enabled());
                    Log.d("ManageProductViewModel", "Product has discount: " + productResult.hasDiscount());
                    updateDiscount(productResult.hasDiscount());
                    showLoading(false);
                },
                error -> showLoading(false)
        ));
    }

    public void findAllEnabledCategories() {
        showLoading(true);
        Log.d("ManageProductViewModel", "Loading all enabled categories");
        categoryRepository.findAllEnabledCategories(new TypeCallback<>() {
            @Override
            public void onSuccess(List<Category> allCategoriesResult) {
                allCategories = allCategoriesResult;
                setCategoryDialogData(allCategoriesResult);
                showLoading(false);
            }

            @Override
            public void onError(ApiErrorResponse error) {
                showLoading(false);
            }
        });
    }

    // Update product on the server
    public void updateProduct(Product product, List<File> imageFiles) {
        showLoading(true);
        Log.d("ManageProductViewModel", "Updating product " + product.name());
        productRepository.updateProduct(product, imageFiles, successCallback(
                () -> {
                    _goBack.postValue(true);
                    showLoading(false);
                },
                error -> showLoading(false)
        ));
    }

    // Enable product on the server
    public void enableProduct(Long id) {
        showLoading(true);
        Log.d("ManageProductViewModel", "Enabling product with id " + id);
        productRepository.enableProductById(id, successCallback(
                () -> {
                    _goBack.postValue(true);
                    showLoading(false);
                },
                error -> showLoading(false)
        ));
    }

    // Disable product on the server
    public void disableProduct(Long id) {
        showLoading(true);
        Log.d("ManageProductViewModel", "Disabling product with id " + id);
        productRepository.disableProductById(id, successCallback(
                () -> {
                    _goBack.postValue(true);
                    showLoading(false);
                },
                error -> showLoading(false)
        ));
    }

    public void validateFields(String name, String description, String price, String shipping, String discountPrice, String stock) {
        if (name.isEmpty() || description.isEmpty() || price.isEmpty() || shipping.isEmpty() || stock.isEmpty() ||
                (viewState.getValue().hasDiscount() && discountPrice.isEmpty())) {
            validationError.setValue(ValidationError.FIELD_EMPTY);
        } else if (Double.parseDouble(price.replace(",", ".")) <= 0) {
            validationError.setValue(ValidationError.INVALID_PRICE);
        } else if (Double.parseDouble(shipping.replace(",", ".")) < 0) {
            validationError.setValue(ValidationError.INVALID_SHIPPING);
        } else if (viewState.getValue().hasDiscount() && Double.parseDouble(discountPrice.replace(",", ".")) < 0) {
            validationError.setValue(ValidationError.INVALID_DISCOUNT_PRICE);
        } else if (productCategories == null || productCategories.isEmpty()) {
            validationError.setValue(ValidationError.CATEGORY_NOT_SELECTED);
        } else if (productImages.getValue() == null || productImages.getValue().isEmpty()) {
            validationError.setValue(ValidationError.NO_IMAGES);
        } else {
            validationError.setValue(null);
            submitProduct(
                    name, description,
                    Double.parseDouble(price.replace(",", ".")),
                    Double.parseDouble(shipping.replace(",", ".")),
                    viewState.getValue().hasDiscount() ? Double.parseDouble(discountPrice.replace(",", ".")) : 0.0,
                    Integer.parseInt(stock));
        }
    }

    private void submitProduct(String name, String description, Double price, Double shipping, Double discountPrice, Integer stock) {
        if (viewState.getValue().isUpdate()) {
            Product existing = product.getValue();
            updateProduct(new Product(
                    existing.id(), existing.enabled(), name, description, price, shipping, productCategories,
                            getExistingImages(productImages.getValue()), existing.shopId(),
                            existing.sold(), existing.rating(), existing.ratingCount(), viewState.getValue().hasDiscount(),
                            discountPrice, stock),
                    fileProvider.getFilesFromUriList(
                            getNewImages(productImages.getValue()),
                            ARG_PRODUCT)
            );

        } else {
            createProduct(new Product(
                    null, true, name, description, price, shipping,
                    productCategories, new ArrayList<>(), shop.id(),
                    0, 0.0, 0, viewState.getValue().hasDiscount(),
                            discountPrice, stock),
                    fileProvider.getFilesFromUriList(
                            productImages.getValue(),
                            ARG_PRODUCT)
            );
        }
    }

    public List<Uri> getNewImages(List<Uri> allImages) {
        List<Uri> newImages = new ArrayList<>();
        for (Uri imageUri : allImages) {
            if (!imageUri.toString().startsWith(BASE_URL)) {
                newImages.add(imageUri);
            }
        }
        return newImages;
    }

    public List<String> getExistingImages(List<Uri> allImages) {
        List<String> existingImages = new ArrayList<>();
        for (Uri imageUri : allImages) {
            if (imageUri.toString().startsWith(BASE_URL)) {
                existingImages.add(imageUri.toString().substring(BASE_URL.length()));
            }
        }
        return existingImages;
    }

    public void onEnableProductSelected() {
        _enableProduct.setValue(true);
    }

    public void onEnableProductConfirmation(Boolean isConfirmed) {
        if (isConfirmed) { enableProduct(product.getValue().id()); }
        else { onCancelActionSelected(); }
    }

    public void onDisableProductSelected() {
        _disableProduct.setValue(true);
    }

    public void onDisableProductConfirmation(Boolean isConfirmed) {
        if (isConfirmed) { disableProduct(product.getValue().id()); }
        else { onCancelActionSelected(); }
    }

    public void onAddImageSelected() {
        if (productImages.getValue().size() < MAX_PRODUCT_IMAGES) { _launchImagePicker.setValue(true); }
        else { _maxImagesReached.setValue(true); }
    }

    public void onImageSelected(Uri uri) {
        productImages.getValue().add(uri);
        newImage.setValue(uri);
    }

    public void onRemoveImageSelected(int position) {
        productImages.getValue().remove(position);
        _imageRemoved.setValue(true);
    }

    public void onCancelActionSelected() {
        cancelledAction.setValue(true);
    }

    public void onSelectCategoriesClicked() {
        if (allCategories == null) { findAllEnabledCategories(); }
        else { setCategoryDialogData(allCategories); }
    }

    public void setCategoryDialogData(List<Category> categories) {
        boolean[] checkedItems = new boolean[categories.size()];

        for (int i = 0; i < categories.size(); i++) {
            checkedItems[i] = productCategories.contains(categories.get(i));
        }

        categoryDialogData.setValue(new ViewUtils.SelectionDialogData<>(
                categories,
                categories.stream().map(Category::name).collect(Collectors.toList()),
                checkedItems));
    }

    public void onCategoriesChanged(Category category, Boolean isChecked) {
        if (isChecked) { productCategories.add(category); }
        else {
            productCategories.removeIf(cat ->
                    category.id().equals(cat.id()));
        }
        setCategoriesInfo();
    }

    private void setCategoriesInfo() {
        _updateCategoriesInfo.postValue(TextUtils.join(
                ", ",
                productCategories.stream().map(Category::name).toArray()));
    }

    public void onSwitchClicked(Boolean isChecked) {
        updateDiscount(isChecked);
    }

    public void onGoBackSelected() {
        _goBack.setValue(true);
    }

}
