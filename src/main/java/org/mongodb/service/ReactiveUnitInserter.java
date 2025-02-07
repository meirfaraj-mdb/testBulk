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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static org.mongodb.utils.DurationUtils.msToHuman;
import static picocli.CommandLine.Command;

@Slf4j
@Command(name = "ReactiveUnitInserter",
        aliases = "runit" ,
        mixinStandardHelpOptions = true,
        description = "Insert using insertMany (Reactive)")
public class ReactiveUnitInserter  extends BaseTest {


    public void test() throws IOException {
        for(int count:writeCommand.appOptions().counts()) {
            long start = System.currentTimeMillis();
            List<Mono<Document>> op = new ArrayList<>();
            for (int i = 0; i <count; i++) {
                op.add(reactiveMongoTemplate.insert(cloned(), "insertTest"));
            }
            waitForAsyncReact(op);
            endTest(count, "Reactive Unit", start, "reactive_unit(" + count + ")");
        }
    }


}
