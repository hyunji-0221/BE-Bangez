package com.bangez.tx.controller;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequestMapping("/api/tx")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        @ApiResponse(responseCode = "404", description = "dd")})
@Log4j2
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
    public IamportResponse<Payment> addIamport(@RequestHeader("Authorization") String accessToken,
                                               @PathVariable("imp_uid") String imp_uid) {
        Long userId = 100L;

        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd/HH:mm:ss");

        IamportResponse<Payment> payment = iamportClient.paymentByImpUid(imp_uid);

        TxModel tx = TxModel.builder()
                .impUid(payment.getResponse().getImpUid())
                .merchantUid(payment.getResponse().getMerchantUid())
                .propertyName(payment.getResponse().getName())
                .propertyAmount(Long.parseLong(payment.getResponse().getAmount().toString()))
                .buyerEmail(payment.getResponse().getBuyerEmail())
                .buyerName(payment.getResponse().getBuyerName())
                .buyerTel(payment.getResponse().getBuyerTel())
                .txDate(date.format(formatter))
                .build();
        service.saveTx(tx);
        pointService.savePoint(payment.getResponse().getAmount(), userId);

        return payment;
    }

    @GetMapping("/list")
    public ResponseEntity<List<TxModel>> getTxList(@RequestHeader("Authorization") String accessToken) {
        List<TxModel> txList = service.getTxList();
        return ResponseEntity.ok(txList);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<TxModel> getTxDetail(@RequestHeader("Authorization") String accessToken,
                                               @PathVariable("id") Long id){
        TxModel tx = service.getTxDetail(id);
        return ResponseEntity.ok(tx);
    }


}