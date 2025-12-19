package com.jhernandez.frontend.bazaar.data.model;

/*
 * Data transfer object for message information.
 */
public record MessageDto(Long id, Long recipientId, String messageDate, String content, Boolean seen) {
}
