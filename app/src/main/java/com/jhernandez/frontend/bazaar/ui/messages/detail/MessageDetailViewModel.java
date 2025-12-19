package com.jhernandez.frontend.bazaar.ui.messages.detail;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.model.Message;
import com.jhernandez.frontend.bazaar.domain.port.MessageRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

/*
 * ViewModel for MessageDetailActivity.
 * Manages the state and data for message details, including loading and marking messages as seen.
 */
public class MessageDetailViewModel extends BaseViewModel {

    private final MessageRepositoryPort messageRepository;
    private final MutableLiveData<MessageDetailViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<Message> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _goBack = new MutableLiveData<>();

    public MessageDetailViewModel(MessageRepositoryPort messageRepository) {
        this.messageRepository = messageRepository;
    }

    public LiveData<MessageDetailViewState> getViewState() {
        return viewState;
    }
    public LiveData<Message> getMessage() {
        return message;
    }
    public LiveData<Boolean> goBackEvent() {
        return _goBack;
    }

    public void initViewState(Long messageId) {
        viewState.setValue(new MessageDetailViewState(false));
        findMessageById(messageId);
    }

    private void showLoading(Boolean isLoading) {
        viewState.setValue(viewState.getValue().withLoading(isLoading));
    }

    private void findMessageById(Long messageId) {
        showLoading(true);
        Log.d("MessageDetailViewModel", "findMessageById: " + messageId);
        messageRepository.findMessageById(messageId, typeCallback(
                message,
                messageResult -> setMessageAsSeen(messageId),
                error -> showLoading(false)
        ));
    }

    private void setMessageAsSeen(Long messageId) {
        Log.d("MessageDetailViewModel", "setMessageAsSeen: " + messageId);
        messageRepository.setMessageAsSeen(messageId, successCallback(
                () -> showLoading(false),
                error -> showLoading(false)
        ));
    }

    public void onGoBackSelected() {
        _goBack.setValue(true);
    }

}
