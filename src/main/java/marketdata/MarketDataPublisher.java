package marketdata;

import rx.subjects.PublishSubject;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MarketDataPublisher implements MarketDataListener {
    private PublishSubject<MarketData> marketDataSubject = null;
    private Set<MarketDataSubscriber> subscriberSet = ConcurrentHashMap.newKeySet();

    public MarketDataPublisher() {
        marketDataSubject = PublishSubject.create();

        marketDataSubject
                .window(1, TimeUnit.SECONDS)
                .debounce(100, TimeUnit.MILLISECONDS).delay(50, TimeUnit.MILLISECONDS)
                .subscribe(marketDataObservable -> marketDataObservable
                        .distinct(MarketData::getRIC)
                        .take(100)
                        .subscribe(this::publishAggregatedMarketData, Throwable::printStackTrace)
                );

    }


    @Override
    public void onMessage(MarketData marketData) {
        marketDataSubject.onNext(marketData);

    }

    public void publishAggregatedMarketData(MarketData marketData) {
        subscriberSet.stream().parallel().forEach(subscriber -> subscriber.onMarketData(marketData));
    }

    public boolean subscribe(MarketDataSubscriber marketDataSubscriber){
        if(!subscriberSet.contains(marketDataSubscriber)){
            subscriberSet.add(marketDataSubscriber);
            return true;
        }
        return false;
    }

    public boolean unsubscribe(MarketDataSubscriber marketDataSubscriber){
        if(subscriberSet.contains(marketDataSubscriber)){
           return subscriberSet.remove(marketDataSubscriber);
        }
        return false;
    }

/*
    private static void publishAggregatedData(List<Integer> integers) {
        System.out.println("publish : " + integers.size() + " : " + LocalDateTime.now());
    }

    public static void main(String[] args) throws InterruptedException {
        PublishSubject<Integer> source = PublishSubject.create();
        Subscription subscribe1 = source.subscribe(new ObserverListner("listener-1"));

        source.window(100, TimeUnit.MILLISECONDS).debounce(100, TimeUnit.MILLISECONDS)
                .take(10)
                .subscribe(MarketDataProcessor::publishAggregatedData, Throwable::printStackTrace);

        IntStream.range(1, 1000).forEach(value -> {
            try {
                source.onNext(value);
                Thread.sleep(10);
            } catch (InterruptedException ie) {
            }
        });


        //  source.onCompleted();
    }


    static class ObserverListner implements Observer<Integer> {
        private String name;

        public ObserverListner(String name) {
            this.name = name;
        }

        @Override
        public void onCompleted() {
            System.out.println("onCompleted : " + this.name);
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("onError : " + this.name);
        }

        @Override
        public void onNext(Integer integer) {
            //System.out.println(this.name + " : "  +integer);
        }
    }

 */
}
