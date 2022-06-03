package marketdata;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@EqualsAndHashCode

public class MarketData{
    private String RIC;
    private double bidPrice;
    private double askPrice;
    private LocalDateTime lastUpdateTime;

    @Override
    public String toString() {
        return "MarketData{" +
                "RIC='" + RIC + '\'' +
                ", bid=" + bidPrice +
                ", ask=" + askPrice +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}