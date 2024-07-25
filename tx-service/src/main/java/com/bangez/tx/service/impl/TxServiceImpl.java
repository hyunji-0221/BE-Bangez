package com.bangez.tx.service.impl;


import com.bangez.tx.domain.model.TxModel;
import com.bangez.tx.repository.PointRepository;
import com.bangez.tx.repository.TxRepository;
import com.bangez.tx.service.TxService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class TxServiceImpl implements TxService {

    private final TxRepository txRepository;
    private final PointRepository pointRepository;

    @Override
    public void saveTx(TxModel tx) {
        txRepository.save(tx);
    }
    @Override
    public List<TxModel> getTxList() {
        return txRepository.findAll();
    }
    @Override
    public TxModel getTxDetail(Long id) {
        return txRepository.findById(id).orElseThrow(null);
    }

}
