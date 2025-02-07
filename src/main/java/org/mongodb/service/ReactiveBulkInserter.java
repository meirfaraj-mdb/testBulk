package org.mongodb.service;

import com.mongodb.bulk.BulkWriteResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mongodb.utils.DurationsRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveBulkOperations;
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
@Command(name = "ReactiveBulkInserter",
        aliases = "rbulk" ,
        mixinStandardHelpOptions = true,
        description = "Reactive Insert using bulk insert")
public class ReactiveBulkInserter  extends BaseTest {

    public void test() throws IOException {
        Document payload = Document.parse(resource.getContentAsString(StandardCharsets.UTF_8));
        for(int count:writeCommand.appOptions().counts()) {
            for(int batchSize:writeCommand.appOptions().batchSizes()) {
                if(batchSize>count){
                    log.info("skip batchSize={} > count={}",batchSize,count);
                    continue;
                }
                long start = System.currentTimeMillis();
                List<Mono<BulkWriteResult>> op = new ArrayList<>();
                for(int batchStart=0;batchStart<count;batchStart+=batchSize) {
                    int batchEnd = Math.min(count, batchStart + batchSize);
                    ReactiveBulkOperations bulk = reactiveMongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "insertTest");
                    IntStream.range(batchStart, batchEnd)
                            .mapToObj(i -> cloned(payload))
                            .forEach(bulk::insert);
                    op.add(bulk.execute());
                }
                waitForAsyncReact(op);
                endTest(count, "Reactive Bulk",  start, "reactive_bulk(" + count + ","+batchSize+")");
            }
        }
    }
}
