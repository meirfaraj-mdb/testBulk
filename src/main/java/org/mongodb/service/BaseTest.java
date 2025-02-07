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
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.CorePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Callable;

import static org.mongodb.utils.DurationUtils.msToHuman;

@Slf4j
public abstract class BaseTest implements Callable<Integer> {

    @Value("classpath:payload.json")
    protected Resource resource;

    @Autowired
    protected DurationsRegistry durationsRegistry;

    @Autowired
    protected WriteCommand writeCommand;


    protected MongoTemplate mongoTemplate;
    protected ReactiveMongoTemplate reactiveMongoTemplate;
    protected Document payload;
    @Override
    public Integer call() throws Exception {
        payload = Document.parse(resource.getContentAsString(StandardCharsets.UTF_8));
        if(writeCommand.appOptions().countAndDrop()){
            mongoTemplate.dropCollection("insertTest");
        }
        for(int i=0;i<writeCommand.appOptions().interation();i++) {
            test();
        }
        return 0;
    }
    public abstract void test()  throws Exception ;

    public boolean dropAndValidate(int count){
        boolean valid=false;
        if(writeCommand.appOptions().countAndDrop()) {
            long cnt = mongoTemplate.count(new Query().withHint("_id_"), "insertTest");
            valid = count == cnt;
            if(!valid) {
                log.warn("Not validated expected count={} but found {}", count, cnt);
            }
            mongoTemplate.dropCollection("insertTest");
        }
        return  valid;
    }

//    protected static void waitForAsyncReactF(List<Flux<Document>> op) {
//        op.stream().map(Flux::collectList).forEach(Mono::block);
//    }


    protected static <T extends CorePublisher<?> > void  waitForAsyncReact(List<T> op) {
        Flux.fromIterable(op)
                .flatMap(mono -> mono)
                .collectList()
                .block();
    }

    protected void endTest(int count, String testName, long start, String testSum) {
        long end = System.currentTimeMillis();
        boolean valid=dropAndValidate(count);
        log.info("{} count={} took:{} validated:{}", testName, count, msToHuman(end - start),valid);
        durationsRegistry.addTiming(testSum, end - start);
    }

    public static Document cloned(Document payload) {
        Document payloadCp = new Document(payload);
        payloadCp.put("_id", new ObjectId());
        return payloadCp;
    }
    protected int[] getCounts() {
        return writeCommand.appOptions().counts();
    }

    public Document cloned() {
        return cloned(payload);
    }

    @Autowired
    public void setReactiveMongoTemplate(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }


    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Document cloned(int i) {
        return cloned();
    }
}
