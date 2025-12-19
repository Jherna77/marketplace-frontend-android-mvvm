package com.jhernandez.frontend.bazaar.ui.product.manage;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_PRODUCT;
import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_PROFILE;
import static com.jhernandez.frontend.bazaar.core.constants.Values.NO_ARG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityManageProductBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.error.ValidationError;
import com.jhernandez.frontend.bazaar.domain.model.Category;
import com.jhernandez.frontend.bazaar.domain.model.Product;
import com.jhernandez.frontend.bazaar.ui.common.adapter.ImageAdapter;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.main.MainActivity;

import java.util.ArrayList;

/*
 * Activity for managing a product.
 * It handles creating, updating, and deleting products, as well as image selection and validation.
 */
public class ManageProductActivity extends AppCompatActivity {

    private ActivityManageProductBinding binding;
    private ManageProductViewModel viewModel;
    private Long productId;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageAdapter adapter;
    private AlertDialog progressDialog;

    public static Intent create(Context context) {
        return new Intent(context, ManageProductActivity.class);
    }

    public static Intent create(Context context, Long id) {
        return new Intent(context, ManageProductActivity.class).putExtra(ARG_PRODUCT, id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        productId = getIntent().getLongExtra(ARG_PRODUCT, NO_ARG);
        initUI();
    }

    private void initUI() {
        initViewModel();
        initImagePicker();
        initViewState();
        initAdapters();
        initProgressDialog();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(ManageProductViewModel.class);
    }

    private void initImagePicker() {
        imagePickerLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        viewModel.onImageSelected(result.getData().getData());
                    } else { viewModel.onCancelActionSelected(); }
                });
    }

    private void initViewState() {
        viewModel.setViewState(productId);
    }

    private void initAdapters() {
        adapter = new ImageAdapter(new ArrayList<>(),
                viewModel::onRemoveImageSelected);
        binding.rvProductImages.setAdapter(adapter);
    }

    private void initProgressDialog() {
        progressDialog = ViewUtils.createProgressDialog(this);
    }

    private void initListeners() {
        binding.tvProductCategories.setOnClickListener(v -> viewModel.onSelectCategoriesClicked());
        binding.swDiscount.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.onSwitchClicked(isChecked));
        binding.tvAddImage.setOnClickListener(v -> viewModel.onAddImageSelected());
        binding.btnSaveProduct.setOnClickListener(v -> saveProduct());
        binding.btnEnableProduct.setOnClickListener(v -> viewModel.onEnableProductSelected());
        binding.btnDisableProduct.setOnClickListener(v -> viewModel.onDisableProductSelected());
        binding.header.btnBack.setOnClickListener(v -> viewModel.onGoBackSelected());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                viewModel.onGoBackSelected();
            }
        });
    }

    private void initObservers() {
        viewModel.getViewState().observe(this, this::updateUI);
        viewModel.getProduct().observe(this, this::setProductInfo);
        viewModel.getCategoryDialogData().observe(this, this::showCategoryDialog);
        viewModel.updateCategoriesInfoEvent().observe(this, this::updateCategoriesInfo);
        viewModel.getProductImages().observe(this, adapter::updateImages);
        viewModel.launchImagePickerEvent().observe(this, this::launchImagePicker);
        viewModel.maxImagesReachedEvent().observe(this, this::showMaxImagesReachedToast);
        viewModel.getNewImage().observe(this, adapter::addImage);
        viewModel.isImageRemoved().observe(this, this::showRemoveImageToast);
        viewModel.enableProductEvent().observe(this, this::enableProductConfirmation);
        viewModel.disableProductEvent().observe(this, this::disableProductConfirmation);
        viewModel.isSuccess().observe(this, this::onSuccess);
        viewModel.isCancelledAction().observe(this, this::onCancelledAction);
        viewModel.goBackEvent().observe(this, go -> goBack());
        viewModel.getValidationError().observe(this, this::onValidationError);
        viewModel.getApiError().observe(this, this::onApiError);
    }

    private void updateUI(ManageProductViewState viewState) {
        binding.header.title.setText(getString(viewState.isUpdate() ? R.string.edit_product : R.string.add_product));
        binding.btnSaveProduct.setText(getString(viewState.isUpdate() ? R.string.update_product : R.string.add_product));
        binding.btnSaveProduct.setVisibility(viewState.isEnabled() ? View.VISIBLE : View.GONE);
        binding.btnEnableProduct.setVisibility(viewState.isEnabled() ? View.GONE : View.VISIBLE);
        binding.btnDisableProduct.setVisibility(viewState.isUpdate() && viewState.isEnabled() ? View.VISIBLE : View.GONE);
        binding.rvProductImages.setVisibility(viewState.isEnabled() ? View.VISIBLE : View.GONE);
        binding.tvProductCategories.setEnabled(viewState.isEnabled());
        binding.etProductName.setEnabled(viewState.isEnabled());
        binding.etProductDescription.setEnabled(viewState.isEnabled());
        binding.etProductPrice.setEnabled(viewState.isEnabled());
        binding.etShipping.setEnabled(viewState.isEnabled());
        binding.tvAddImage.setEnabled(viewState.isEnabled());
        if (viewState.isLoading()) { progressDialog.show(); } else { progressDialog.dismiss(); }
        binding.tvProductCategories.setEnabled(!viewState.isLoading());
        binding.btnSaveProduct.setEnabled(!viewState.isLoading());
        binding.btnEnableProduct.setEnabled(!viewState.isLoading());
        binding.btnDisableProduct.setEnabled(!viewState.isLoading());
        binding.swDiscount.setChecked(viewState.hasDiscount());
        binding.etDiscountPrice.setEnabled(viewState.hasDiscount());
    }

    private void setProductInfo(Product product) {
        binding.etProductName.setText(product.name());
        binding.etProductDescription.setText(product.description());
        binding.etProductPrice.setText(String.format(getString(R.string.float_format), product.price()));
        binding.etShipping.setText(String.format(getString(R.string.float_format), product.shipping()));
        binding.etStock.setText(String.valueOf(product.stock()));
        binding.etDiscountPrice.setText(String.format(getString(R.string.float_format), product.discountPrice()));
    }

    private void showCategoryDialog(ViewUtils.SelectionDialogData<Category> data) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.select_categories)
                .setMultiChoiceItems(
                        data.itemNames().toArray(new String[0]),
                        data.checkedItems(),
                        (dialog, which, isChecked) ->
                            viewModel.onCategoriesChanged(data.items().get(which), isChecked)
                )
                .setPositiveButton(R.string.ok_resp, null)
                .show();
    }

    private void updateCategoriesInfo(String categories) {
        binding.tvProductCategories.setText(categories);
    }

    private void launchImagePicker(Boolean launch) {
        if (launch) { selectImage(imagePickerLauncher); }
    }

    private void saveProduct() {
        viewModel.validateFields(
                binding.etProductName.getText().toString().trim(),
                binding.etProductDescription.getText().toString().trim(),
                binding.etProductPrice.getText().toString().trim(),
                binding.etShipping.getText().toString().trim(),
                binding.etDiscountPrice.getText().toString().trim(),
                binding.etStock.getText().toString().trim()
        );
    }

    private void enableProductConfirmation(Boolean show) {
        if (show) {
            ViewUtils.confirmActionDialog(
                    this,
                    getString(R.string.enable_product_warn),
                    getString(R.string.enable_product_msg),
                    viewModel::onEnableProductConfirmation);
        }
    }

    private void disableProductConfirmation(Boolean show) {
        if (show) {
            ViewUtils.confirmActionDialog(
                    this,
                    getString(R.string.disable_product_warn),
                    getString(R.string.disable_product_msg),
                    viewModel::onDisableProductConfirmation);
        }
    }

    private void onSuccess(Boolean success) {
        ViewUtils.showSuccessToast(this, success);
    }

    private void onCancelledAction(Boolean cancelled) {
        ViewUtils.showCancelToast(this, cancelled);
    }

    private void showMaxImagesReachedToast(Boolean show) {
        if (show) { ViewUtils.showToast(this, R.string.toast_max_images); }
    }

    private void showRemoveImageToast(Boolean show) {
        if (show) { ViewUtils.showToast(this, R.string.toast_image_removed); }
    }

    private void onValidationError(ValidationError error) {
        if (error != null ) {
            ViewUtils.showErrorOnTextView(this, error, binding.tvErrors);
            binding.main.smoothScrollTo(0, binding.tvErrors.getTop());
        }
    }

    private void onApiError(ApiErrorResponse error) {
        if (error != null) {
            ViewUtils.showErrorOnTextView(this, error, binding.tvErrors);
            binding.main.smoothScrollTo(0, binding.tvErrors.getTop());
        }
    }

    private void goBack() {
        startActivity(MainActivity.create(this, ARG_PROFILE));
        finish();
    }

    // Launch the image picker (gallery or camera)
    private void selectImage(ActivityResultLauncher<Intent> imagePickerLauncher) {
        ImagePicker.with(this)
                .crop()
                .compress(1024) // Image size will be reduced to 1 MB
                .maxResultSize(1080, 1080) // Image resolution will be reduced to 1080x1080
                .createIntent(intent -> {
                    imagePickerLauncher.launch(intent);
                    return null;
                });
    }

}
