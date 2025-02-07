package org.mongodb.utils;

import org.springframework.stereotype.Component;

import java.util.LongSummaryStatistics;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import static org.mongodb.utils.DurationUtils.msToHuman;

@Component
public class DurationsRegistry {
    ConcurrentMap<String,LongSummaryStatistics> results = new ConcurrentHashMap<>();

    public void addTiming(String test,long duration){
        results.computeIfAbsent(test,k->new LongSummaryStatistics()).accept(duration);
    }

    public void printAsTable(){
        System.out.println("===========RESULTS===========");
        results.entrySet().stream().forEach(
                e->
                        System.out.println(e.getKey()+" : min:"+msToHuman(e.getValue().getMin())
                        +";max:"+msToHuman(e.getValue().getMax())+";avg:"+msToHuman(Math.round(e.getValue().getAverage()))
                        +";count:"+e.getValue().getCount())
        );
        System.out.println("=============================");
    }
}
