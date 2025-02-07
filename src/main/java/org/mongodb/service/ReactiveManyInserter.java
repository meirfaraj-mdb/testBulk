package org.mongodb.service;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mongodb.utils.DurationsRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import static org.mongodb.utils.DurationUtils.msToHuman;
import static picocli.CommandLine.Command;

@Slf4j
@Command(name = "ReactiveManyInserter",
        aliases = "rmany" ,
        mixinStandardHelpOptions = true,
        description = "Reactive Insert using insertMany")
public class ReactiveManyInserter  extends BaseTest {

    public void test() throws IOException {
        for(int count:writeCommand.appOptions().counts()) {
            for(int batchSize:writeCommand.appOptions().batchSizes()) {
                if(batchSize>count){
                    log.info("skip batchSize={} > count={}",batchSize,count);
                    continue;
                }
                long start = System.currentTimeMillis();
                List<Flux<Document>> op = new ArrayList<>();
                for(int batchStart=0;batchStart<count;batchStart+=batchSize) {
                    int batchEnd = Math.min(count, batchStart + batchSize);
                    op.add(reactiveMongoTemplate.insertAll(Mono.just(IntStream.range(batchStart, batchEnd)
                            .mapToObj(this::cloned).toList()), "insertTest"));
                }
                waitForAsyncReact(op);
                endTest(count, "Reactive Many", start, "reactive_many(" + count + ","+batchSize+")");

            }
        }
    }


}
