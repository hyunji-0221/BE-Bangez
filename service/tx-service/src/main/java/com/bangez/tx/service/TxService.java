package com.bangez.tx.service;

import java.util.List;

import com.bangez.tx.domain.dto.PointDto;
import com.bangez.tx.domain.dto.TxDto;
import com.bangez.tx.domain.model.TxModel;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

public interface TxService {

    void saveTx(IamportResponse<Payment> payment, Long userId);

    List<TxDto> getTxList();

    TxDto getTxDetail(Long id);

    default TxModel convertToModel(TxDto txDto) {
        return TxModel.builder()
                .txId(txDto.getTxId())
                .impUid(txDto.getImpUid())
                .merchantUid(txDto.getMerchantUid())
                .propertyName(txDto.getPropertyName())
                .propertyAmount(txDto.getPropertyAmount())
                .buyerEmail(txDto.getBuyerEmail())
                .buyerName(txDto.getBuyerName())
                .buyerTel(txDto.getBuyerTel())
                .txDate(txDto.getTxDate())
                .userId(txDto.getUserId())
                .apartmentId(txDto.getApartmentId())
                .officetelId(txDto.getOfficetelId())
                .build();
    }

    default TxDto convertToDto(TxModel txModel) {
        return TxDto.builder()
                .txId(txModel.getTxId())
                .impUid(txModel.getImpUid())
                .merchantUid(txModel.getMerchantUid())
                .propertyName(txModel.getPropertyName())
                .propertyAmount(txModel.getPropertyAmount())
                .buyerEmail(txModel.getBuyerEmail())
                .buyerName(txModel.getBuyerName())
                .buyerTel(txModel.getBuyerTel())
                .txDate(txModel.getTxDate())
                .userId(txModel.getUserId())
                .apartmentId(txModel.getApartmentId())
                .officetelId(txModel.getOfficetelId())
                .build();
    }

}
