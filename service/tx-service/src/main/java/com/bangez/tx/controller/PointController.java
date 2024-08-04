package com.bangez.tx.controller;

import com.bangez.tx.domain.dto.PointDto;
import com.bangez.tx.domain.model.PointModel;
import com.bangez.tx.service.impl.PointServiceImpl;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/point")
public class PointController {
    private final PointServiceImpl service;

    @GetMapping("/detail/{userId}")
    public ResponseEntity<Integer> getPointDetail(@PathVariable("userId") Long id){
        return ResponseEntity.ok(service.getPointDetail(id));
    }

    @PutMapping("/deduction/{userId}")
    public ResponseEntity<PointDto> deductionPoint(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(service.deductionPoint(userId));
    }
}

