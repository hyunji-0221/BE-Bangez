package com.bangez.tx.controller;

import com.bangez.tx.domain.dto.TxDto;
import com.bangez.tx.service.impl.PointServiceImpl;
import com.bangez.tx.service.impl.TxServiceImpl;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TxController {

    private final TxServiceImpl txService;
    private final PointServiceImpl pointService;

    private IamportClient iamportClient;

    @Value("${imp.api.key}")
    private String apiKey;

    @Value("${imp.api.secretKey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }

    @PostMapping("/add/{imp_uid}/{userId}")
    public IamportResponse<Payment> addIamport(@PathVariable("imp_uid") String imp_uid,
                                               @PathVariable("userId") Long userId) {
        IamportResponse<Payment> payment = iamportClient.paymentByImpUid(imp_uid);
        txService.saveTx(payment, userId);
        pointService.savePoint(payment.getResponse().getAmount(), userId);
        return payment;
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity<List<TxDto>> getTxList(@PathVariable("userId") Long id) {
        List<TxDto> txList = txService.getTxList();
        return ResponseEntity.ok(txList);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<TxDto> getTxDetail(@PathVariable("id") Long id){
        log.info("디테일 메소드 id: {}", id);
        return ResponseEntity.ok(txService.getTxDetail(id));
    }


}
