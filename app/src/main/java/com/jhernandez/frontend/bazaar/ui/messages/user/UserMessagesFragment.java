package com.jhernandez.frontend.bazaar.ui.messages.user;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.core.di.ViewModelFactory;
import com.jhernandez.frontend.bazaar.databinding.FragmentUserMessagesBinding;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.ui.common.util.ViewUtils;
import com.jhernandez.frontend.bazaar.ui.messages.adapter.MessageListAdapter;
import com.jhernandez.frontend.bazaar.ui.messages.detail.MessageDetailActivity;

import java.util.ArrayList;

import lombok.NoArgsConstructor;
import lombok.NonNull;

/*
 * Fragment for displaying user messages.
 * Manages the message list, filtering, and navigation to message details.
 */
@NoArgsConstructor
public class UserMessagesFragment extends Fragment {

    private FragmentUserMessagesBinding binding;
    private UserMessagesViewModel viewModel;
    private MessageListAdapter adapter;
    private Context context;
    private LifecycleOwner owner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserMessagesBinding.inflate(inflater, container, false);
        initUI();
        return binding.getRoot();
    }

    private void initUI() {
        context = requireContext();
        owner = getViewLifecycleOwner();
        initViewModel();
        initViewState();
        initAdapters();
        initListeners();
        initObservers();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication()))
                .get(UserMessagesViewModel.class);
    }

    private void initViewState() {
        viewModel.initViewState();
    }

    private void initAdapters() {
        adapter = new MessageListAdapter(
                new ArrayList<>(),
                new MessageListAdapter.OnMessageActionListener() {
                    @Override
                    public void onMessageClicked(Long messageId) {
                        viewModel.onCheckMessageSelected(messageId);
                    }

                    @Override
                    public void onDeleteClicked(Long messageId) {
                        deleteMessageConfirmation(messageId);
                    }
                }
        );
        binding.rvMessages.setAdapter(adapter);
    }

    private void initListeners() {
        binding.btnFilter.setOnClickListener(v -> viewModel.onFilterResultsSelected());
        binding.chipAscending.setOnClickListener(v -> viewModel.onOrderSelected(binding.chipAscending.getTag().toString()));
        binding.chipDescending.setOnClickListener(v -> viewModel.onOrderSelected(binding.chipDescending.getTag().toString()));
    }

    private void initObservers() {
        viewModel.getViewState().observe(owner, this::updateUI);
        viewModel.getFilteredMessages().observe(owner, adapter::updateMessages);
        viewModel.goToMessageDetailEvent().observe(owner, this::goToMessageDetail);
        viewModel.isCancelledAction().observe(owner, this::onCancelledAction);
        viewModel.getApiError().observe(owner, this::onApiError);
    }

    private void updateUI(UserMessagesViewState viewState) {
        binding.btnFilter.setEnabled(viewState.hasResults());
        binding.llFilter.setVisibility(viewState.filterEnabled() ? View.VISIBLE : View.GONE);
        binding.tvInfo.setVisibility(!viewState.hasResults() ? View.VISIBLE : View.GONE);
    }

    private void deleteMessageConfirmation(Long messageId) {
        ViewUtils.confirmActionDialog(
                context,
                getString(R.string.delete_message_warn),
                getString(R.string.delete_message_msg),
                isConfirmed -> viewModel.onDeleteMsgConfirmation(isConfirmed, messageId));
    }

    private void onCancelledAction(Boolean cancelled) {
        ViewUtils.showCancelToast(context, cancelled);
    }

    private void onApiError(ApiErrorResponse error) {
        ViewUtils.errorDialog(context, error);
    }

    private void goToMessageDetail(Long messageId) {
        startActivity(MessageDetailActivity.create(context, messageId));
        requireActivity().finish();
    }

}