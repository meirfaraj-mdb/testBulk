package org.mongodb.service;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mongodb.options.AppOptions;
import org.mongodb.utils.DurationsRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import static org.mongodb.utils.DurationUtils.msToHuman;
import static picocli.CommandLine.*;

@Slf4j
@Command(name = "ManyInserter",
        aliases = "many" ,
        mixinStandardHelpOptions = true,
        description = "Insert using insertMany")
public class ManyInserter  extends BaseTest  {

    public void test() throws IOException {
        Document payload = Document.parse(resource.getContentAsString(StandardCharsets.UTF_8));
        for(int count:writeCommand.appOptions().counts()) {
            for(int batchSize:writeCommand.appOptions().batchSizes()) {
                if(batchSize>count){
                    log.info("skip batchSize={} > count={}",batchSize,count);
                    continue;
                }
                long start = System.currentTimeMillis();
                for(int batchStart=0;batchStart<count;batchStart+=batchSize) {
                    int batchEnd=Math.min(count,batchStart+batchSize);
                    mongoTemplate.insert(
                            IntStream.range(batchStart, batchEnd)
                            .mapToObj(i -> cloned(payload)).toList(),
                            "insertTest");
                }
                endTest(count, "Many insert",  start, "many(" + count + ","+batchSize+")");
            }
        }
    }



}
