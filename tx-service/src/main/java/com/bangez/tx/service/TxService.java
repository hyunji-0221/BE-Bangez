package com.bangez.tx.service;

import java.util.List;

import com.bangez.tx.domain.model.TxModel;

public interface TxService {

    void saveTx(TxModel tx);

    List<TxModel> getTxList();

    TxModel getTxDetail(Long id);
    
}
