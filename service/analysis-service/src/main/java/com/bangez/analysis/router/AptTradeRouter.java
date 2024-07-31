package com.bangez.analysis.router;
import com.bangez.analysis.repository.AptTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AptTradeRouter {
    private final AptTradeRepository repository;

    public Mono<?> execute(String select, String date){

        return switch (select){
            case "1" -> repository.plotGraphAvgCostByDate();
            case "2" -> repository.plotGraphSaleCountByDate();
            case "3" -> repository.plotGraphSalesCountByRegionForMonth(date);
            case "4" -> repository.plotGraphCostByArea();
//            case "5" -> service.plotGraphCostRaisebyRegionFor3Month();
//            case "6" -> service.costRaiseTop5Bottom5ForYear();
            case "7" -> repository.tardeCountRaiseTop5ForMonth();

            default -> null;
        };
    }
    
}
