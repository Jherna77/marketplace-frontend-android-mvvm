package com.jhernandez.frontend.bazaar.data.mapper;

import com.jhernandez.frontend.bazaar.data.model.MessageDto;
import com.jhernandez.frontend.bazaar.domain.model.Message;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between MessageDto and Message domain model.
 */
public class MessageMapper {

    public static Message toDomain(MessageDto messageDto) {
        return new Message(
                messageDto.id(),
                messageDto.recipientId(),
                messageDto.messageDate(),
                messageDto.content(),
                messageDto.seen());
    }

    public static List<Message> toDomainList(List<MessageDto> messageDtoList) {
        return messageDtoList.stream()
                .map(MessageMapper::toDomain)
                .collect(Collectors.toList());
    }
}
