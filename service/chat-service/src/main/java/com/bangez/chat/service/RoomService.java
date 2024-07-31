package com.bangez.chat.service;

import com.bangez.chat.domain.dto.Messenger;
import com.bangez.chat.domain.model.RoomModel;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoomService {
    Mono<RoomModel> openRoom(String userId, String receiverId);

    Flux<RoomModel> getRoomList(String userId);

    Mono<Messenger> deleteRoom(String roomId);
}
