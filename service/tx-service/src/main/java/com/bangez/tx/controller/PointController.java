package com.bangez.tx.controller;

import com.bangez.tx.domain.MessageVo;
import com.bangez.tx.domain.dto.PointDto;
import com.bangez.tx.service.impl.PointServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {
    private final PointServiceImpl service;
    @GetMapping("/detail/{userId}")
    public ResponseEntity<Integer> getPointDetail(@PathVariable("userId") Long id){
        return ResponseEntity.ok(service.getPointDetail(id));
    }
    @PutMapping("/deduction/{userId}")
    public ResponseEntity<MessageVo> deductionPoint(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(service.deductionPoint(userId));
    }

}


