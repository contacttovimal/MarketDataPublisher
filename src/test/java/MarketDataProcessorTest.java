
import marketdata.MarketData;
import marketdata.MarketDataListener;
import marketdata.MarketDataProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

class MarketDataProcessorTest {
    final List<String> RICs = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AK", "AL", "AM", "AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ","BA", "BB", "BC", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BK", "BL", "BM", "BN", "BO", "BP", "BQ", "BR", "BS", "BT", "BU", "BV","BW", "BX", "BY", "BZ", "CA", "CB", "CC", "CD", "CE", "CF", "CG", "CH", "CI", "CJ", "CK", "CL", "CM", "CN", "CO", "CP", "CQ", "CR","CS", "CT", "CU", "CV", "CW", "CX", "CY");
//    final List<String> RICs = Arrays.asList("A","B", "C");
    @ParameterizedTest(name = "{0} RICS - {1} updates per 100MS, {2} expected")
    @CsvSource({"100,10,100", "101,10,100", "100,11,200", "101,11,200"})
    void publish100MsgPerSec(int noOfRics, int noOfRicRecords, int expectedPublishCount) throws InterruptedException {
        List<MarketData> publishedRicList = new ArrayList<>();
        MarketDataListener marketDataProcessor = new MarketDataProcessor() {
            @Override
            public void publishAggregatedMarketData(MarketData data) {
                super.publishAggregatedMarketData(data);
                publishedRicList.add(data);
            }
        };
        final int msSleepTimePerRicPublish = 100;
        publishMarketData(marketDataProcessor, RICs.stream().limit(noOfRics).collect(Collectors.toList()), msSleepTimePerRicPublish, noOfRicRecords);
        Assert.assertEquals(expectedPublishCount, publishedRicList.size());
    }

    // Ensure each symbol will not have more than one update per second
    @ParameterizedTest(name = "when 2 RIC with {0} update entries & RIC update received per 100MS, {1} update entries publishing for each RIC is expected")
    @CsvSource({"0,0", "5,1", "11,2"})
    void notMoreThanOneUpdatePerSec(int noOfUpdateEntriesForEachRIC, int expectedNoOfPublishingForEachRIC) throws InterruptedException {
        HashMap<String, Integer> publishCount = new HashMap<>();
        MarketDataListener marketDataProcessor = new MarketDataProcessor() {
            @Override
            public void publishAggregatedMarketData(MarketData data) {
                super.publishAggregatedMarketData(data);
                publishCount.put(data.getRIC(), publishCount.getOrDefault(data.getRIC(), 0) + 1);
            }
        };
        final int msSleepTimePerRicPublish = 100;
        List<String> inputRICs = RICs.stream().limit(2).collect(Collectors.toList());
        publishMarketData(marketDataProcessor, inputRICs, msSleepTimePerRicPublish, noOfUpdateEntriesForEachRIC);
        inputRICs.forEach(s -> {
            Assert.assertEquals(expectedNoOfPublishingForEachRIC, publishCount.getOrDefault(s, 0).intValue());
        });
    }

    // Ensure each symbol will always have the latest market data when it is published
    @Test
    void publishLatestMarketDataAlways() throws InterruptedException {
        HashMap<String, MarketData> publishedEntry = new HashMap<>();
        MarketDataListener marketDataProcessor = new MarketDataProcessor() {
            @Override
            public void publishAggregatedMarketData(MarketData data) {
                super.publishAggregatedMarketData(data);
                publishedEntry.put(data.getRIC(), data);
            }
        };
        final int msSleepTimePerRicPublish = 100;
        final int noOfUpdateEntriesPerRIC = 11;
        List<String> inputRICS = RICs.stream().limit(3).collect(Collectors.toList());
        publishMarketData(marketDataProcessor, inputRICS, msSleepTimePerRicPublish, noOfUpdateEntriesPerRIC);
        final double delta = 0.000001;
        inputRICS.forEach(s -> {
            Assert.assertEquals(11, publishedEntry.get(s).getAsk(), delta);
            Assert.assertEquals(11, publishedEntry.get(s).getBid(), delta);
        });
    }

    private void publishMarketData(MarketDataListener marketDataListener, List<String> rics, int sleepTimePerRecord, int noOfRicRecords) throws InterruptedException {
//        System.out.println( " : Publishing start : " + LocalDateTime.now());
        for (int i = 1; i <= noOfRicRecords; i++) {
            for (String ric : rics) {
                marketDataListener.onMessage(new MarketData(ric,i,i,LocalDateTime.now()));
            }
            Thread.sleep(sleepTimePerRecord);
        }
//        System.out.println( " : Publishing done : " + LocalDateTime.now());
    }



}
