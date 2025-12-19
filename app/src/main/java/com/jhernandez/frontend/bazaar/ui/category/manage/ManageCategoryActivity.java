package com.jhernandez.frontend.bazaar.ui.category.manage;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_CATEGORY;
import static com.jhernandez.frontend.bazaar.core.constants.Values.NO_ARG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityManageCategoryBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.error.ValidationError;
import com.jhernandez.frontend.bazaar.domain.model.Category;
import com.jhernandez.frontend.bazaar.ui.admin.AdminActivity;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;

/*
 * Activity for managing (adding/editing) a category.
 * Handles user interactions for category details, image selection, and saving changes.
 */
public class ManageCategoryActivity extends AppCompatActivity {

    private ActivityManageCategoryBinding binding;
    private ManageCategoryViewModel viewModel;
    private Long categoryId;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private AlertDialog progressDialog;

    public static Intent create(Context context) {
        return new Intent(context, ManageCategoryActivity.class);
    }

    public static Intent create(Context context, Long id) {
        return new Intent(context, ManageCategoryActivity.class).putExtra(ARG_CATEGORY, id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        categoryId = getIntent().getLongExtra(ARG_CATEGORY, NO_ARG);
        initUI();
    }

    private void initUI() {
        initViewModel();
        initImagePicker();
        initViewState();
        initProgressDialog();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(ManageCategoryViewModel.class);
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
        viewModel.setViewState(categoryId);
    }

    private void initProgressDialog() {
        progressDialog = ViewUtils.createProgressDialog(this);
    }

    private void initListeners() {
        binding.addImage.setOnClickListener(v -> viewModel.onAddImageSelected());
        binding.removeImage.setOnClickListener(v -> viewModel.onRemoveImageSelected());
        binding.btnSaveCategory.setOnClickListener(v -> saveCategory());
        binding.btnDisableCategory.setOnClickListener(v -> viewModel.onDisableCategorySelected());
        binding.btnEnableCategory.setOnClickListener(v -> viewModel.onEnableCategorySelected());
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
        viewModel.getCategory().observe(this, this::setCategoryInfo);
        viewModel.getCategoryImage().observe(this, this::setCategoryImage);
        viewModel.addImageEvent().observe(this, add -> addImage());
        viewModel.removeImageEvent().observe(this, remove -> removeImage());
        viewModel.enableCategoryEvent().observe(this, enable -> enableCategory());
        viewModel.disableCategoryEvent().observe(this, disable -> disableCategory());
        viewModel.isSuccess().observe(this, this::onSuccessToast);
        viewModel.goBackEvent().observe(this, go -> goBack());
        viewModel.isCancelledAction().observe(this, this::onCancelledAction);
        viewModel.getValidationError().observe(this, this::onValidationError);
        viewModel.getApiError().observe(this, this::onApiError);
    }

    private void updateUI(ManageCategoryViewState viewState) {
        binding.header.title.setText(getString(viewState.isUpdate() ? R.string.edit_category : R.string.add_category));
        binding.btnSaveCategory.setText(getString(viewState.isUpdate() ? R.string.update_category : R.string.add_category));
        binding.btnSaveCategory.setVisibility(viewState.isEnabled() ? View.VISIBLE : View.GONE);
        binding.btnEnableCategory.setVisibility(viewState.isEnabled() ? View.GONE : View.VISIBLE);
        binding.btnDisableCategory.setVisibility(viewState.isUpdate() && viewState.isEnabled() ? View.VISIBLE : View.GONE);
        binding.etCategoryName.setEnabled(viewState.isEnabled());
        binding.addImage.setVisibility(!viewState.hasImage() && viewState.isEnabled() ? View.VISIBLE : View.GONE);
        binding.removeImage.setVisibility(viewState.hasImage() && viewState.isEnabled() ? View.VISIBLE : View.GONE);
        if (!viewState.hasImage()) { binding.categoryImage.setImageResource(R.drawable.img_image); }
        if (viewState.isLoading()) { progressDialog.show(); }
        else { progressDialog.dismiss(); }
    }

    private void setCategoryInfo(Category category) {
        binding.etCategoryName.setText(category.name());
        if (category.imageUrl() != null)
            ViewUtils.showImageOnImageView(this, category.imageUrl(), binding.categoryImage);
    }

    private void setCategoryImage(Uri categoryImage) {
        binding.categoryImage.setImageURI(categoryImage);
    }

    private void addImage() {
        selectImage(imagePickerLauncher);
    }

    private void removeImage() {
        ViewUtils.confirmActionDialog(
                this,
                getString(R.string.remove_img_warn),
                getString(R.string.remove_img_msg),
                viewModel::onRemoveImageConfirmation);
    }

    private void enableCategory() {
        ViewUtils.confirmActionDialog(
                this,
                getString(R.string.enable_category_warn),
                getString(R.string.enable_category_msg),
                viewModel::onEnableCategoryConfirmation);
    }

    private void disableCategory() {
        ViewUtils.confirmActionDialog(
                this,
                getString(R.string.disable_category_warn),
                getString(R.string.disable_category_msg),
                viewModel::onDisableCategoryConfirmation);
    }

    private void saveCategory() {
        viewModel.validateFields(binding.etCategoryName.getText().toString().trim());
    }

    private void onCancelledAction(Boolean cancelled) {
        ViewUtils.showCancelToast(this, cancelled);
    }

    private void onSuccessToast(Boolean success) {
        ViewUtils.showSuccessToast(this, success);
    }

    private void onValidationError(ValidationError error) {
        ViewUtils.showErrorOnTextView(this, error, binding.tvErrors);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.showErrorOnTextView(this, error, binding.tvErrors);
    }

    private void goBack() {
        startActivity(AdminActivity.create(this, ARG_CATEGORY));
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
