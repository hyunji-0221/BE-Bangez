package com.bangez.tx.service;

import java.math.BigDecimal;
import java.util.Optional;

import com.bangez.tx.domain.model.PointModel;

public interface PointService {
     void savePoint(BigDecimal amount, Long userId) ;
     Optional<PointModel> getPointDetail(Long id);

     PointModel deductionPoint(Long userId);
    
}
