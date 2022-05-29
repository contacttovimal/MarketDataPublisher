package marketdata;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class MarketData implements Comparable {
    private String RIC;

    public String getRIC() {
        return RIC;
    }

    public double getBid() {
        return bid;
    }

    public double getAsk() {
        return ask;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    private double bid;
    private double ask;
    private LocalDateTime lastUpdateTime;

    public MarketData(String RIC, double bid, double ask, LocalDateTime lastUpdateTime) {
        this.RIC = RIC;
        this.bid = bid;
        this.ask = ask;
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public int compareTo(Object other) {
        if(other != null && other instanceof MarketData){
            MarketData otherMarketData = (MarketData) other;
            if(getRIC()!= null && otherMarketData.getRIC()!= null && getRIC().equals(otherMarketData.getRIC())){
                return otherMarketData.getLastUpdateTime().compareTo(getLastUpdateTime());
            }else if(getRIC()!= null && otherMarketData.getRIC()!= null){
                return otherMarketData.getRIC().compareTo(getRIC());
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MarketData that = (MarketData) o;

        return this.RIC.equals(that.getRIC());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.RIC);
    }

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