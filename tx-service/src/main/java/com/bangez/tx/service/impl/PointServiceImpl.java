package com.bangez.tx.service.impl;



import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bangez.tx.domain.model.PointModel;
import com.bangez.tx.repository.PointRepository;
import com.bangez.tx.service.PointService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;

    LocalDateTime date = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd/HH:mm:ss");
    @Override
    public void savePoint(BigDecimal amount, Long userId) {
        PointModel model = pointRepository.findByUserId(userId).orElseGet(null);
        int point = model.getPoint();
        int intAmount = amount.intValue();
        point += intAmount / 500;
        if(intAmount % 500 != 0){
            throw new IllegalArgumentException("500원 단위로 결제해주세요.");
        }else{
            PointModel pointModel = PointModel.builder()
                    .pointId(model.getPointId())
                    .point(point)
                    .userId(userId)
                    .lastChargeDate(date.format(formatter))
                    .build();
            pointRepository.save(pointModel);
        }
    }
    @Override
    public Optional<PointModel> getPointDetail(Long id) {
        return pointRepository.findByUserId(id);
    }
    @Override
    public PointModel deductionPoint(Long userId) {
        int deductedPoint = 0;
        PointModel point = pointRepository.findByUserId(userId).orElseGet(null);
        if(point.getPoint() <= 0) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }else {
            deductedPoint = point.getPoint() - 1;
        }
        return pointRepository.save(PointModel.builder()
                .pointId(point.getPointId())
                .userId(userId)
                .point(deductedPoint)
                .lastChargeDate(date.format(formatter))
                .build());
    }
}

