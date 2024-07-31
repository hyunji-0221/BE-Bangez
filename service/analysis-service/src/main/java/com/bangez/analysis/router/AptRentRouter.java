package com.bangez.analysis.router;

import com.bangez.analysis.repository.AptRentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AptRentRouter {

    private final AptRentRepository repository;

    public Mono<?> execute(String select, String date){

        return switch (select){
            case "1" -> repository.plotGraphAvgCostByDate();
            case "2" -> repository.plotGraphSaleCountByDate();
            case "3" -> repository.plotGraphSalesCountByRegionForMonth(date);

            default -> null;
        };
    }
}