package com.bangez.chat.service.impl;
import com.bangez.chat.domain.dto.Messenger;
import com.bangez.chat.domain.model.ChatModel;
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
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{
    private final ChatRepository chatRepository;
    private final Map<String, Sinks.Many<ServerSentEvent<ChatModel>>> chatSinks = new HashMap<>();
    private final Map<String, Sinks.Many<ServerSentEvent<ChatModel>>> notificationSinks = new HashMap<>();

    @Override
    public Flux<ServerSentEvent<ChatModel>> connectChat(String roomId) {
        log.info("connectChat service roomId : {}", roomId);

        Sinks.Many<ServerSentEvent<ChatModel>> sink = chatSinks.computeIfAbsent(roomId, key -> {
            log.info("Creating new sink for roomId : {}", roomId);
            Sinks.Many<ServerSentEvent<ChatModel>> chatSink = Sinks.many().replay().all();
            chatRepository.findByChatRoomId(roomId)
                    .map(chat -> ServerSentEvent.builder(chat).build())
                    .doOnNext(chatSink::tryEmitNext)
                    .subscribe();
            return chatSink;
        });

        Flux<ServerSentEvent<ChatModel>> heartbeatFlux = Flux.interval(Duration.ofSeconds(30))
                .map(tick -> ServerSentEvent.<ChatModel>builder()
                        .event("heartbeat")
                        .data(new ChatModel()) // 빈 ChatModel을 데이터로 보냄
                        .build());

        Flux<ServerSentEvent<ChatModel>> chatFlux = sink.asFlux()
                .mergeWith(heartbeatFlux);

        log.info("Existing sink for roomId : {}", roomId);
        return chatFlux.doOnCancel(() -> handleCancel(roomId));
    }

    @Override
    public Flux<ServerSentEvent<ChatModel>> connectionNotification(String userId){
        Sinks.Many<ServerSentEvent<ChatModel>> sink = Sinks.many().replay().all();
        chatRepository.findByReceiverIdAndReadFalse(userId)
                .map(chat -> ServerSentEvent.builder(chat)
                        .event("notification")
                        .data(chat)
                        .build())
                .doOnNext(sink::tryEmitNext)
                .subscribe();
        return sink.asFlux();
    }

    @Override
    public Mono<ChatModel> saveMessage(ChatModel chatModel) {
        log.info("saveMessage service: {}", chatModel);
        chatModel.setTimeStamp(Instant.now().toString());
        return chatRepository.save(chatModel)
                .doOnSuccess(savedMessage -> {
                    log.info("Saved message: {}", savedMessage);
                    Sinks.Many<ServerSentEvent<ChatModel>> sink = chatSinks.get(savedMessage.getChatRoomId());
                    if (sink != null) {
                        sink.tryEmitNext(ServerSentEvent.builder(savedMessage).build());
                    }
                    if (!savedMessage.isRead()) { // savedMessage.isRead() == false
                        notifyUnreadMessage(savedMessage);
                    }

                });
    }

    @Override
    public Mono<Messenger> markMessageRead(String roomId, String userId) {
        log.info("챗서비스의 마크메시지리드");
        return chatRepository.findByChatRoomIdAndReceiverIdAndReadFalse(roomId, userId)
                .flatMap(chat -> {
                    chat.setRead(true);
                    return chatRepository.save(chat);
                })
                .then(Mono.just(Messenger.builder().message("SUCCESS TO CHANGE MESSAGES STATUS").build()));
    }

    private void handleCancel(String roomId) {
        log.info("service doOnCancel roomId : {}", roomId);
        Sinks.Many<ServerSentEvent<ChatModel>> sink = chatSinks.remove(roomId);
        if (sink != null) sink.tryEmitComplete();
    }

    private void notifyUnreadMessage(ChatModel chatModel) {
        Sinks.Many<ServerSentEvent<ChatModel>> notificationSink = notificationSinks.get(chatModel.getReceiverId());
        if (notificationSink != null) {
            notificationSink.tryEmitNext(ServerSentEvent.builder(chatModel).build());
        }
    }

    private void handleNotificationCancel(String userId) {
        log.info("service doOnCancel userId : {}", userId);
        Sinks.Many<ServerSentEvent<ChatModel>> notificationSink = notificationSinks.remove(userId);
        if (notificationSink != null) notificationSink.tryEmitComplete();
    }
    
}
