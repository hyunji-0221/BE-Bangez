package com.bangez.chat.service.impl;

import com.bangez.chat.domain.dto.ChatDto;
import com.bangez.chat.domain.dto.Messenger;
import com.bangez.chat.repository.ChatRepository;
import com.bangez.chat.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final Map<String, Sinks.Many<ServerSentEvent<ChatDto>>> chatSinks = new HashMap<>();
    private final Map<String, Sinks.Many<ServerSentEvent<ChatDto>>> notificationSinks = new HashMap<>();

    @Override
    public Flux<ServerSentEvent<ChatDto>> connectChat(String roomId) {
        Sinks.Many<ServerSentEvent<ChatDto>> sink = chatSinks.computeIfAbsent(roomId, key -> {
            Sinks.Many<ServerSentEvent<ChatDto>> chatSink = Sinks.many().replay().all();
            chatRepository.findByChatRoomId(roomId)
                    .map(this::convertToDto)
                    .map(chat -> ServerSentEvent.builder(chat).build())
                    .doOnNext(chatSink::tryEmitNext)
                    .subscribe();
            return chatSink;
        });

        Flux<ServerSentEvent<ChatDto>> chatFlux = sink.asFlux()
                .mergeWith(heartBeat());

        return chatFlux.doOnCancel(() -> handleCancel(roomId));
    }

    @Override
    public Flux<ServerSentEvent<ChatDto>> connectionNotification(String userId) {
        Sinks.Many<ServerSentEvent<ChatDto>> chatSink = Sinks.many().replay().all();
        chatRepository.findByReceiverIdAndReadFalse(userId)
                .map(this::convertToDto)
                .map(chat -> ServerSentEvent.builder(chat)
                        .event("notification")
                        .data(chat)
                        .build())
                .doOnNext(chatSink::tryEmitNext)
                .subscribe();

        Flux<ServerSentEvent<ChatDto>> chatFlux = chatSink.asFlux()
                .mergeWith(heartBeat());

        return chatFlux.doOnCancel(() -> handleNotificationCancel(userId));
    }

    @Override
    public Mono<ChatDto> saveMessage(ChatDto chatDto) {
        chatDto.setTimeStamp(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        return chatRepository.save(convertToEntity(chatDto))
                .map(this::convertToDto)
                .doOnSuccess(savedMessage -> {
                    Sinks.Many<ServerSentEvent<ChatDto>> sink = chatSinks.get(savedMessage.getChatRoomId());
                    if (sink != null) {
                        sink.tryEmitNext(ServerSentEvent.builder(savedMessage).build());
                    }
                    if (!savedMessage.isRead()) {
                        notifyUnreadMessage(savedMessage);
                    }
                });
    }

    @Override
    public Mono<Messenger> markMessageRead(String roomId, String userId) {
        return chatRepository.findByChatRoomIdAndReceiverIdAndReadFalse(roomId, userId)
                .flatMap(chat -> {
                    chat.setRead(true);
                    return chatRepository.save(chat);
                })
                .then(Mono.just(Messenger.builder().message("SUCCESS TO CHANGE MESSAGES STATUS").build()));
    }

    private void handleCancel(String roomId) {
        Sinks.Many<ServerSentEvent<ChatDto>> sink = chatSinks.remove(roomId);
        if (sink != null) sink.tryEmitComplete();
    }

    private void notifyUnreadMessage(ChatDto chatDto) {
        Sinks.Many<ServerSentEvent<ChatDto>> notificationSink = notificationSinks.get(chatDto.getReceiverId());
        if (notificationSink != null) {
            notificationSink.tryEmitNext(ServerSentEvent.builder(chatDto).build());
        }
    }

    private void handleNotificationCancel(String userId) {
        Sinks.Many<ServerSentEvent<ChatDto>> notificationSink = notificationSinks.remove(userId);
        if (notificationSink != null) notificationSink.tryEmitComplete();
    }

    private Flux<ServerSentEvent<ChatDto>> heartBeat() {
        return Flux.interval(Duration.ofSeconds(86400000))
                .map(tick -> ServerSentEvent.<ChatDto>builder()
                        .event("heartbeat")
                        .data(new ChatDto())
                        .build());
    }

}
