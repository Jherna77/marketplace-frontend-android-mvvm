package com.jhernandez.frontend.bazaar.data.repository;

import com.jhernandez.frontend.bazaar.data.api.ApiService;
import com.jhernandez.frontend.bazaar.data.mapper.MessageMapper;
import com.jhernandez.frontend.bazaar.data.network.CallbackDelegator;
import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Message;
import com.jhernandez.frontend.bazaar.domain.port.MessageRepositoryPort;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * Repository class for managing message-related operations.
 */
@RequiredArgsConstructor
public class MessageRepository implements MessageRepositoryPort {

    private final ApiService apiService;

    @Override
    public void findMessageById(Long id, TypeCallback<Message> callback) {
        apiService.findMessageById(id)
                .enqueue(CallbackDelegator.delegate(
                        "getMessageById",
                        response ->
                                callback.onSuccess(MessageMapper.toDomain(response)),
                                callback::onError));
    }

    @Override
    public void findMessagesByRecipientId(Long recipientId, TypeCallback<List<Message>> callback) {
        apiService.findMessagesByRecipientId(recipientId)
                .enqueue(CallbackDelegator.delegate(
                        "getMessagesByRecipientId",
                        response ->
                                callback.onSuccess(MessageMapper.toDomainList(response)),
                                callback::onError));
    }

    @Override
    public void hasNewMessages(Long recipientId, TypeCallback<Boolean> callback) {
        apiService.hasNewMessages(recipientId)
                .enqueue(CallbackDelegator.delegate(
                        "hasNewMessages",
                                callback::onSuccess,
                                callback::onError));
    }

    @Override
    public void setMessageAsSeen(Long id, SuccessCallback callback) {
        apiService.setMessageAsSeen(id)
                .enqueue(CallbackDelegator.delegate("setMessageAsSeen", callback));
    }

    @Override
    public void deleteMessageById(Long id, TypeCallback<List<Message>> callback) {
        apiService.deleteMessageById(id)
                .enqueue(CallbackDelegator.delegate(
                        "deleteMessageById",
                        response ->
                                callback.onSuccess(MessageMapper.toDomainList(response)),
                                callback::onError));
    }

}
