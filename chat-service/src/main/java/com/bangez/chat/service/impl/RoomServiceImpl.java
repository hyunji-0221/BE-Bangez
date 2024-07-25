package com.bangez.chat.service.impl;
import com.bangez.chat.domain.dto.Messenger;
import com.bangez.chat.domain.model.RoomModel;
import com.bangez.chat.repository.RoomRepository;
import com.bangez.chat.service.RoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;


@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    LocalDateTime time = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

    private final RoomRepository roomRepository;

    @Override
    public Mono<RoomModel> openRoom(String userId, String receiverId) {
        log.info("service time : {}", time);
        return roomRepository.getRoomModelBySenderIdAndReceiverId(userId, receiverId)
                .map(roomModel -> roomModel)
                .switchIfEmpty(roomRepository.save(RoomModel.builder()
                        .roomTitle("test1")
                        .senderId(userId)
                        .receiverId(receiverId)
                        .createDate(time)
                        .build())
                );
    }

    @Override
    public Flux<RoomModel> getRoomList(String userId) {
        return roomRepository.getRoomModelsBySenderId(userId);
    }

    @Override
    public Mono<Messenger> deleteRoom(String roomId) {
        return roomRepository.existsById(roomId)
                .flatMap(exists -> {
                    if (exists) {
                        return roomRepository.deleteById(roomId)
                                .then(Mono.just(Messenger.builder().message("SUCCESS").build()));
                    } else {
                        return Mono.just(Messenger.builder().message("FAILURE").build());
                    }
                });
    }
}
