package com.jhernandez.frontend.bazaar.ui.messages.user;

import static com.jhernandez.frontend.bazaar.core.constants.Values.TAG_ASCENDING;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jhernandez.frontend.bazaar.domain.model.Message;
import com.jhernandez.frontend.bazaar.domain.model.User;
import com.jhernandez.frontend.bazaar.domain.port.MessageRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.common.BaseViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
 * ViewModel for UserMessagesFragment.
 * Manages the message list, filtering, and navigation to message details.
 */
public class UserMessagesViewModel extends BaseViewModel {

    private final MessageRepositoryPort messageRepository;
    private final User recipient;
    private final MutableLiveData<UserMessagesViewState> viewState = new MutableLiveData<>();
    private final MutableLiveData<List<Message>> filteredMessages = new MutableLiveData<>();
    private final MutableLiveData<Long> _goToMessageDetail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cancelledAction = new MutableLiveData<>();
    private List<Message> allMessages;
    private String selectedOrder;

    public UserMessagesViewModel(MessageRepositoryPort messageRepository, SessionRepositoryPort sessionRepository) {
        this.messageRepository = messageRepository;
        this.recipient = sessionRepository.getSessionUser();
    }

    public LiveData<UserMessagesViewState> getViewState() {
        return viewState;
    }
    public LiveData<List<Message>> getFilteredMessages() {
        return filteredMessages;
    }
    public LiveData<Long> goToMessageDetailEvent() {
        return _goToMessageDetail;
    }
    public LiveData<Boolean> isCancelledAction() {
        return cancelledAction;
    }

    public void initViewState() {
        viewState.setValue(new UserMessagesViewState(false, false));
        selectedOrder = TAG_ASCENDING;
        findMessagesByRecipientId();
    }

    private void updateFilter(Boolean filter) {
        viewState.setValue(viewState.getValue().withFilter(filter));
    }

    private void updateResults(Boolean results) {
        viewState.setValue(viewState.getValue().withResults(results));
    }

    private void findMessagesByRecipientId() {
        Log.d("MessagesViewModel", "Loading all messages for recipient " + recipient.id());
        messageRepository.findMessagesByRecipientId(recipient.id(), typeCallback(
                filteredMessages,
                messagesResult -> {
                    allMessages = messagesResult;
                    updateResults(messagesResult != null && !messagesResult.isEmpty());
                },
                error -> allMessages = new ArrayList<>()));
    }

    private void deleteMessage(Long messageId) {
        Log.d("MessagesViewModel", "Deleting message " + messageId);
        messageRepository.deleteMessageById(messageId, typeCallback(
                filteredMessages,
                messagesResult -> {
                    allMessages = messagesResult;
                    updateResults(messagesResult != null && !messagesResult.isEmpty());
                },
                error -> allMessages = new ArrayList<>()));
    }

    public void onCheckMessageSelected(Long messageId) {
        _goToMessageDetail.setValue(messageId);
    }

    public void onDeleteMsgConfirmation(Boolean isConfirmed, Long messageId) {
        if (isConfirmed) { deleteMessage(messageId); }
        else { onCancelActionSelected(); }
    }

    public void onCancelActionSelected() {
        cancelledAction.setValue(true);
    }

    public void onFilterResultsSelected() {
        updateFilter(!viewState.getValue().filterEnabled());
    }

    public void onOrderSelected(String order) {
        selectedOrder = order;
        applyFilters();
    }

    private void applyFilters() {
        if (selectedOrder.equals(TAG_ASCENDING)) {
            allMessages.sort(Comparator.comparing(Message::id));
        } else {
            allMessages.sort(Comparator.comparing(Message::id).reversed());
        }

        filteredMessages.setValue(allMessages);
    }
}
