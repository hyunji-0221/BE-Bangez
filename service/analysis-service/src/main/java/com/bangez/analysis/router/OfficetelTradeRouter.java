package com.bangez.analysis.router;
import com.bangez.analysis.repository.OfficetelTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OfficetelTradeRouter {
    private final OfficetelTradeRepository repository;

    public Mono<?> execute(String select, String date){

        return switch (select){
            case "1" -> repository.plotGraphAvgCostByDate();
            case "2" -> repository.plotGraphSaleCountByDate();
            case "3" -> repository.plotGraphSalesCountByRegionForMonth(date);
            case "4" -> repository.plotGraphCostByArea();

            default -> null;
        };
    }
    
}
