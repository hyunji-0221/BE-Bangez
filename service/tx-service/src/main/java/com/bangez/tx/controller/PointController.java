package com.bangez.tx.controller;

import com.bangez.tx.domain.model.PointModel;
import com.bangez.tx.service.impl.PointServiceImpl;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/point")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        @ApiResponse(responseCode = "404", description = "dd")})
@Log4j2
@RequiredArgsConstructor
public class PointController {
    private final PointServiceImpl service;



    @GetMapping("/detail/{id}")
    public ResponseEntity<PointModel> getPointDetail(@RequestHeader("Authorization") String accessToken,
                                                     @PathVariable("id") Long id){
        return ResponseEntity.ok(service.getPointDetail(id).orElseThrow(null));
    }

    @PutMapping("/deduction/{id}") // 포인트 차감 / accesstoken 코드 받으면 @PathVariable 지우고, accessToken 으로 id 찾기
    public ResponseEntity<PointModel> deductionPoint(@RequestHeader("Authorization") String accessToken,
                                                     @PathVariable("id") Long userId){
        log.info("id: {}",userId);
        return ResponseEntity.ok(service.deductionPoint(userId));
    }

}

