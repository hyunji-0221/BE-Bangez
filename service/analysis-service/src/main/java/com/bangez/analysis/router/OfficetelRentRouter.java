package com.bangez.analysis.router;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.bangez.analysis.repository.OfficetelRentRepository;


@Component
@RequiredArgsConstructor
public class OfficetelRentRouter {
    private final OfficetelRentRepository repository;

    public Mono<?> execute(String select, String date){

        return switch (select){
            case "1" -> repository.plotGraphAvgCostByDate();
            case "2" -> repository.plotGraphSaleCountByDate();
            case "3" -> repository.plotGraphSalesCountByRegionForMonth(date);
            default -> null;
        };
    }
}
