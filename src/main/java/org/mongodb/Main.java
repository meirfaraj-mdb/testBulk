package org.mongodb;

import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.mongodb.service.WriteCommand;
import org.mongodb.utils.DurationsRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

import java.rmi.registry.Registry;
import java.util.Arrays;

@Slf4j
@SpringBootApplication
public class Main
        implements CommandLineRunner, ExitCodeGenerator {
    private final IFactory factory;

    private int exitCode;
    private final WriteCommand writeCommand;

    @Autowired
    DurationsRegistry registry;

    Main(IFactory factory, WriteCommand writeCommand) {
        this.factory = factory;
        this.writeCommand = writeCommand;
    }


    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(Main.class, args)));
    }

    @Override
    public void run(String... args) {
        log.info("command line runner {}", Arrays.toString(args));
        exitCode = new CommandLine(writeCommand, factory).execute(args);
        registry.printAsTable();
    }


    @Override
    public int getExitCode() {
        return exitCode;
    }
}