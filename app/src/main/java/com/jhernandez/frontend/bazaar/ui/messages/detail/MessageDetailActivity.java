package com.jhernandez.frontend.bazaar.ui.messages.detail;

import static com.jhernandez.frontend.bazaar.core.constants.Values.ARG_MESSAGE;
import static com.jhernandez.frontend.bazaar.core.constants.Values.NO_ARG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.ActivityMessageDetailBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.model.Message;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.main.MainActivity;

/*
 * Activity for displaying the details of a specific message.
 * Handles loading state, error handling, and navigation back to the main activity.
 */
public class MessageDetailActivity extends AppCompatActivity {

    private ActivityMessageDetailBinding binding;
    private MessageDetailViewModel viewModel;
    private Long messageId;
    private AlertDialog progressDialog;

    public static Intent create(Context context, Long id) {
        return new Intent(context, MessageDetailActivity.class).putExtra(ARG_MESSAGE, id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        messageId = getIntent().getLongExtra(ARG_MESSAGE, NO_ARG);
        initUI();
    }

    private void initUI() {
        initViewModel();
        initViewState();
        initProgressDialog();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(MessageDetailViewModel.class);
    }

    private void initViewState() {
        viewModel.initViewState(messageId);
    }

    private void initProgressDialog() {
        progressDialog = ViewUtils.createProgressDialog(this);
    }

    private void initListeners() {
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
        viewModel.getMessage().observe(this, this::setMessageInfo);
        viewModel.goBackEvent().observe(this, go -> goBack());
        viewModel.getApiError().observe(this, this::onApiError);
    }

    private void updateUI(MessageDetailViewState viewState) {
        binding.header.title.setText(R.string.message_detail);
        if (viewState.isLoading()) { progressDialog.show(); } else { progressDialog.dismiss(); }
    }

    private void setMessageInfo(Message message) {
        binding.tvMessageDate.setText(String.format(getString(R.string.message_date_tv), message.messageDate()));
        binding.messageContent.setText(message.content());
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(this, error);
    }

    private void goBack() {
        startActivity(MainActivity.create(this, ARG_MESSAGE));
        finish();
    }

}