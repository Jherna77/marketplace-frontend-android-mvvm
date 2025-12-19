package com.jhernandez.frontend.bazaar.domain.model;

/*
 * Record representing a Message entity.
 */
public record Message(Long id, Long recipientId, String messageDate, String content, Boolean seen) {
}
