package com.bangez.chat.controller;
import com.bangez.chat.domain.dto.Messenger;
import com.bangez.chat.domain.dto.RoomDto;
import com.bangez.chat.domain.model.RoomModel;
import com.bangez.chat.service.impl.RoomServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomServiceImpl roomServiceImpl;
    @PostMapping(value = "/open-room")
    public Mono<RoomDto> saveRoom(@RequestBody RoomDto roomDto) {
        return roomServiceImpl.openRoom(roomDto);
    }
    @GetMapping(value = "/get-room-list/{userId}")
    public Flux<RoomDto> getRoomList(@PathVariable("userId") String userId) {
        return roomServiceImpl.getRoomList(userId);
    }
    @DeleteMapping(value = "/delete-room/{roomId}")
    public Mono<Messenger> deleteRoom(@PathVariable("roomId") String roomId) {
        return roomServiceImpl.deleteRoom(roomId);
    }
}
