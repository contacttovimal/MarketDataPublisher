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
    private double bid;
    private double ask;
    private LocalDateTime lastUpdateTime;

    @Override
    public String toString() {
        return "MarketData{" +
                "RIC='" + RIC + '\'' +
                ", bid=" + bid +
                ", ask=" + ask +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}