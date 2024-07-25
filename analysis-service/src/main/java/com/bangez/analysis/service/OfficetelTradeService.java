package com.bangez.analysis.service;

import java.util.Map;

public interface OfficetelTradeService {
    public Map<?, ?> plotGraphAvgCostByDate();

    public Map<?,?> plotGraphSalesCountByRegionForMonth(String date);

    public Map<?,?> plotGraphSaleCountByDate() ;

    public Map<?,?> plotGraphCostByArea() ;

    public Map<?,?> plotGraphCostRaiseByRegionFor3Month() ;
    
}
