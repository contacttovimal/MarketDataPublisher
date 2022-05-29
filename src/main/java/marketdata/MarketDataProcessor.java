package marketdata;

import rx.Observer;
import rx.subjects.PublishSubject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MarketDataProcessor implements MarketDataListener{
    private PublishSubject<MarketData> marketDataFeed = null;


    public MarketDataProcessor() {
        marketDataFeed = PublishSubject.create();
        marketDataFeed
                // 1 second non-overlapping sliding window
                .window(1,TimeUnit.SECONDS)
                .subscribe(marketDataObservable -> marketDataObservable
                        .sorted()
                        .distinct(MarketData::getRIC)
                    .take(100)
                    .subscribe(this::publishAggregatedMarketData, Throwable::printStackTrace)
                );

    }


    @Override
    public void onMessage(MarketData marketData) {
//        System.out.println("onMessage : "+marketData);
        marketDataFeed.onNext(marketData);

    }

    public void publishAggregatedMarketData(MarketData marketData) {
            //on publish to do
           //System.out.println("publish : " + marketData + " : " + LocalDateTime.now());
    }



    public static void main(String[] args) throws InterruptedException{
       /* PublishSubject<Integer> source = PublishSubject.create();
        source.window(1,TimeUnit.SECONDS);
// It will get 1, 2, 3, 4 and onComplete
        Subscription subscribe1 = source.subscribe(new ObserverListner("listener-1"));

        source.window(1000, TimeUnit.MILLISECONDS).take(10).forEach(integerObservable -> {
            System.out.println(LocalDateTime.now() + " : " + integerObservable.take(10));
        });

        IntStream.range(1,1000).forEach(value-> {
            try {
                source.onNext(value);
                Thread.sleep(10);
            }catch(InterruptedException ie){}
        });*/
        MarketData marketData1 = new MarketData("A",10,10,LocalDateTime.now());
        Thread.sleep(10);
        MarketData marketData2 = new MarketData("A",1,1,LocalDateTime.now());
        Thread.sleep(10);
        MarketData marketData3 = new MarketData("A",5,5,LocalDateTime.now());
        List<MarketData> dataList = new ArrayList<>();
        dataList.add(marketData1);dataList.add(marketData2);dataList.add(marketData3);
        System.out.println(dataList.stream().sorted().limit(2).collect(Collectors.toList()));



      //  source.onCompleted();
    }

   static class ObserverListner implements Observer<Integer> {
        private String name;

        public ObserverListner(String name){
            this.name = name;
        }
        @Override
        public void onCompleted() {
            System.out.println("onCompleted : "+ this.name);
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("onError : "+ this.name);
        }

        @Override
        public void onNext(Integer integer) {
            //System.out.println(this.name + " : "  +integer);
        }
    }
}
