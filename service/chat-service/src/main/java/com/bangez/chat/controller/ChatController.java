package com.bangez.chat.controller;
import com.bangez.chat.domain.dto.ChatDto;
import com.bangez.chat.domain.dto.Messenger;
import com.bangez.chat.domain.model.ChatModel;
import com.bangez.chat.service.impl.ChatServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatServiceImpl chatServiceImpl;
    @GetMapping(path = "/sse/{roomId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ChatDto>> connectSSE(@PathVariable("roomId") String roomId) {
        return chatServiceImpl.connectChat(roomId);
    }
    @PostMapping("/save")
    public ResponseEntity<Mono<ChatDto>> sendMessage(@RequestBody ChatDto chatDto) {
        return ResponseEntity.ok(chatServiceImpl.saveMessage(chatDto));
    }
    @GetMapping("/read/{roomId}/{userId}")
    public ResponseEntity<Mono<Messenger>> markMessageRead(@PathVariable("roomId") String roomId,@PathVariable("userId") String userId){
        return ResponseEntity.ok(chatServiceImpl.markMessageRead(roomId, userId));
    }
    @GetMapping(value = "/notifications/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ChatDto>> connectNotification(@PathVariable("userId") String userId) {
        return chatServiceImpl.connectionNotification(userId);
    }
}
