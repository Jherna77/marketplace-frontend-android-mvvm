package com.jhernandez.frontend.bazaar.domain.port;

import com.jhernandez.frontend.bazaar.domain.callback.SuccessCallback;
import com.jhernandez.frontend.bazaar.domain.callback.TypeCallback;
import com.jhernandez.frontend.bazaar.domain.model.Message;

import java.util.List;

/*
 * Interface representing the MessageRepositoryPort.
 */
public interface MessageRepositoryPort {

    void findMessageById(Long id, TypeCallback<Message> callback);
    void findMessagesByRecipientId(Long recipientId, TypeCallback<List<Message>> callback);
    void hasNewMessages(Long recipientId, TypeCallback<Boolean> callback);
    void setMessageAsSeen(Long id, SuccessCallback callback);
    void deleteMessageById(Long id, TypeCallback<List<Message>> callback);

}
