package org.mongodb.service;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.types.ObjectId;
import org.mongodb.options.AppOptions;
import org.mongodb.utils.DurationsRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import static picocli.CommandLine.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import static org.mongodb.utils.DurationUtils.*;

@Slf4j
@Command(name = "UnitInserter",
        aliases = "unit" ,
        mixinStandardHelpOptions = true,
        description = "Insert using insertMany")
public class UnitInserter extends BaseTest {

    public void test() throws Exception {
        for(int count: getCounts()) {
            long start = System.currentTimeMillis();
            for (int i = 0; i <count; i++) {
                mongoTemplate.insert(cloned(), "insertTest");
            }
            endTest(count, "Unit insert", start, "unit(" + count + ")");
        }
    }


}
