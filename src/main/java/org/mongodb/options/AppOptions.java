package org.mongodb.options;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

import static picocli.CommandLine.*;

@Data
@Accessors(fluent = true)
public class AppOptions {
    @Option(names = {"-H", "--help"},
            usageHelp = true,
            description = "display this help message")
    private boolean usageHelpRequested;

    @Option(
            names = {"-c", "--count" },
            arity = "0..",
            split = ",", splitSynopsisLabel = ",",
            description = "count of documents to insert",
            defaultValue = "1000",
            showDefaultValue = Help.Visibility.ALWAYS)
    private int[] counts;

    @Option(
            names = {"-b", "--batchSize"},
            arity = "0..",
            split = ",", splitSynopsisLabel = ",",
            description = "batch size",
            defaultValue = "100",
            showDefaultValue = Help.Visibility.ALWAYS)
    private int[] batchSizes;


    @Option(
            names = {"-i", "--iteration"},
            description = "number of time test are conduced",
            defaultValue = "20",
            showDefaultValue = Help.Visibility.ALWAYS)
    private int interation;


    @Option(
            names = {"-cd", "--countAndDrop"},
            description = "Count and drop at each time",
            defaultValue = "true",
            showDefaultValue = Help.Visibility.ALWAYS)
    private boolean countAndDrop;

}
