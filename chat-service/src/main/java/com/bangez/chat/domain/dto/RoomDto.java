package com.bangez.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component
public class RoomDto {
    private String id;
    private String roomTitle;
    private String senderId;
    private String receiverId;

    private String createDate;
}
