package com.bangez.tx.controller;

import com.bangez.tx.domain.dto.TxDto;
import com.bangez.tx.domain.model.TxModel;
import com.bangez.tx.service.impl.PointServiceImpl;
import com.bangez.tx.service.impl.TxServiceImpl;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TxController {

    private final TxServiceImpl service;
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

    @PostMapping("/add/{imp_uid}")
    public IamportResponse<Payment> addIamport(@PathVariable("imp_uid") String imp_uid,
                                               @PathVariable("userId") Long userId) {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd/HH:mm:ss");

        IamportResponse<Payment> payment = iamportClient.paymentByImpUid(imp_uid);

        TxDto tx = TxDto.builder()
                .impUid(payment.getResponse().getImpUid())
                .merchantUid(payment.getResponse().getMerchantUid())
                .propertyName(payment.getResponse().getName())
                .propertyAmount(Long.parseLong(payment.getResponse().getAmount().toString()))
                .buyerEmail(payment.getResponse().getBuyerEmail())
                .buyerName(payment.getResponse().getBuyerName())
                .buyerTel(payment.getResponse().getBuyerTel())
                .txDate(date.format(formatter))
                .userId(userId)
                .build();
        service.saveTx(tx);
        pointService.savePoint(payment.getResponse().getAmount(), userId);

        return payment;
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity<List<TxDto>> getTxList(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(service.getTxList());
    }

    @GetMapping("/detail/{userId}")
    public ResponseEntity<TxDto> getTxDetail(@PathVariable("userId") Long id) {
        TxDto tx = service.getTxDetail(id);
        return ResponseEntity.ok(tx);
    }


}