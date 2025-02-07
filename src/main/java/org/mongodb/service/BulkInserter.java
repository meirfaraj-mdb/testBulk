package org.mongodb.service;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mongodb.utils.DurationsRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import static org.mongodb.utils.DurationUtils.msToHuman;
import static picocli.CommandLine.Command;

@Slf4j
@Command(name = "BulkInserter",
        aliases = "bulk" ,
        mixinStandardHelpOptions = true,
        description = "Insert using bulk insert")
public class BulkInserter  extends BaseTest  {

    public void test() throws IOException {
        for(int count:writeCommand.appOptions().counts()) {
            for(int batchSize:writeCommand.appOptions().batchSizes()) {
                if(batchSize>count){
                    log.info("skip batchSize={} > count={}",batchSize,count);
                    continue;
                }
                long start = System.currentTimeMillis();
                for(int batchStart=0;batchStart<count;batchStart+=batchSize) {
                    int batchEnd = Math.min(count, batchStart + batchSize);
                    BulkOperations bulk = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "insertTest");
                    IntStream.range(batchStart, batchEnd)
                            .mapToObj(this::cloned)
                            .forEach(bulk::insert);
                    bulk.execute();
                }
                endTest(count, "Bulk insert", start, "bulk(" + count + ","+batchSize+")");
            }
        }
    }

}
