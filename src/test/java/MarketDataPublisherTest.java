
import marketdata.MarketData;
import marketdata.MarketDataListener;
import marketdata.MarketDataPublisher;
import marketdata.MarketDataSubscriber;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

class MarketDataPublisherTest {
    //101 RICs
    final List<String> RICs = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AK", "AL", "AM", "AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ","BA", "BB", "BC", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BK", "BL", "BM", "BN", "BO", "BP", "BQ", "BR", "BS", "BT", "BU", "BV","BW", "BX", "BY", "BZ", "CA", "CB", "CC", "CD", "CE", "CF", "CG", "CH", "CI", "CJ", "CK", "CL", "CM", "CN", "CO", "CP", "CQ", "CR","CS", "CT", "CU", "CV", "CW", "CX", "CY");
//    final List<String> RICs = Arrays.asList("A","B", "C");
    @ParameterizedTest(name = "{0} RICS - {1} updates per 100MS, {2} expected")
    @CsvSource({"100,10,100", "101,10,100", "100,11,200", "101,11,200"})
    void publish100MsgPerSec(int noOfRics, int noOfRicRecords, int expectedPublishCount) throws InterruptedException {
        List<MarketData> publishedRicList = new ArrayList<>();
        MarketDataSubscriber marketDataSubscriber = new MarketDataSubscriber() {
            @Override
            public void onMarketData(MarketData marketData) {
                publishedRicList.add(marketData);
            }
        };
        MarketDataPublisher marketDataPublisher = new MarketDataPublisher();
        marketDataPublisher.subscribe(marketDataSubscriber);

        MarketDataListener marketDataListener = new MarketDataListener() {
            @Override
            public void onMessage(MarketData data) {
                marketDataPublisher.onMessage(data);
            }
        };
        final int msSleepTimePerRicPublish = 100;
        publishMarketData(marketDataListener, RICs.stream().limit(noOfRics).collect(Collectors.toList()), msSleepTimePerRicPublish, noOfRicRecords);
        Assert.assertEquals(expectedPublishCount, publishedRicList.size());
    }

    @ParameterizedTest(name = "when 2 RIC publish with {0} and update received per 100MS, {1} updates for each RIC is expected")
    @CsvSource({"0,0", "5,1", "11,2"})
    void notMoreThanOneUpdatePerSec(int noOfUpdateEntriesForEachRIC, int expectedNoOfPublishingForEachRIC) throws InterruptedException {
        HashMap<String, Integer> publishCount = new HashMap<>();
        MarketDataSubscriber marketDataSubscriber = new MarketDataSubscriber() {
            @Override
            public void onMarketData(MarketData marketData) {
                publishCount.put(marketData.getRIC(), publishCount.getOrDefault(marketData.getRIC(), 0) + 1);
            }
        };
        MarketDataPublisher marketDataPublisher = new MarketDataPublisher();
        marketDataPublisher.subscribe(marketDataSubscriber);

        MarketDataListener marketDataListener = new MarketDataListener() {
            @Override
            public void onMessage(MarketData data) {
                marketDataPublisher.onMessage(data);
            }
        };

        final int msSleepTimePerRicPublish = 100;
        List<String> inputRICs = RICs.stream().limit(2).collect(Collectors.toList());
        publishMarketData(marketDataListener, inputRICs, msSleepTimePerRicPublish, noOfUpdateEntriesForEachRIC);
        inputRICs.forEach(s -> {
            Assert.assertEquals(expectedNoOfPublishingForEachRIC, publishCount.getOrDefault(s, 0).intValue());
        });
    }

    @Test
    void publishLatestMarketDataAlways() throws InterruptedException {
        HashMap<String, MarketData> publishedEntry = new HashMap<>();

        MarketDataSubscriber marketDataSubscriber = new MarketDataSubscriber() {
            @Override
            public void onMarketData(MarketData marketData) {
                publishedEntry.put(marketData.getRIC(), marketData);
            }
        };
        MarketDataPublisher marketDataPublisher = new MarketDataPublisher();
        marketDataPublisher.subscribe(marketDataSubscriber);

        MarketDataListener marketDataListener = new MarketDataListener() {
            @Override
            public void onMessage(MarketData data) {
                marketDataPublisher.onMessage(data);
            }
        };

        final int msSleepTimePerRicPublish = 100;
        final int noOfUpdateEntriesPerRIC = 11;
        List<String> inputRICS = RICs.stream().limit(3).collect(Collectors.toList());
        publishMarketData(marketDataListener, inputRICS, msSleepTimePerRicPublish, noOfUpdateEntriesPerRIC);
        final double delta = 0.000001;
        inputRICS.forEach(s -> {
            Assert.assertEquals(11, publishedEntry.get(s).getAskPrice(), delta);
            Assert.assertEquals(11, publishedEntry.get(s).getBidPrice(), delta);
        });
    }

    private void publishMarketData(MarketDataListener marketDataListener, List<String> rics, int sleepTimePerRecord, int noOfRicRecords) throws InterruptedException {
        for (int i = 1; i <= noOfRicRecords; i++) {
            for (String ric : rics) {
                marketDataListener.onMessage(new MarketData(ric,i,i,LocalDateTime.now()));
            }
            Thread.sleep(sleepTimePerRecord);
        }
    }



}
