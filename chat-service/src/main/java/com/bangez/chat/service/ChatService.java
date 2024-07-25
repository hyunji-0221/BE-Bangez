package com.bangez.chat.service;
import com.bangez.chat.domain.dto.Messenger;
import com.bangez.chat.domain.model.ChatModel;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
public interface ChatService {
    Flux<ServerSentEvent<ChatModel>> connectChat(String roomId) ;

    Mono<ChatModel> saveMessage(ChatModel chatModel);

    Mono<Messenger> markMessageRead(String roomId, String userId);

    Flux<ServerSentEvent<ChatModel>> connectionNotification(String userId);
    
}
