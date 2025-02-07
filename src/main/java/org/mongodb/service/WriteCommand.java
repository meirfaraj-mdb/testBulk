package org.mongodb.service;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.mongodb.options.AppOptions;
import org.springframework.stereotype.Component;
import static picocli.CommandLine.*;

import java.util.concurrent.Callable;

@Slf4j
@Component
@Accessors(fluent = true)
@Data
@Command(
        name = "write",
        synopsisSubcommandLabel = "(unit | many | bulk)",
        subcommands = {
                UnitInserter.class,
                ManyInserter.class,
                BulkInserter.class,
                ReactiveUnitInserter.class,
                ReactiveManyInserter.class,
                ReactiveBulkInserter.class,
        },
        mixinStandardHelpOptions = true,
        subcommandsRepeatable = true,
        scope = ScopeType.INHERIT
)
public class WriteCommand  implements Callable<Integer> {


    @Mixin
    private AppOptions appOptions;

    @Override
    public Integer call() throws Exception {
        log.info("In WriteCommand");
        if(appOptions.usageHelpRequested())
            usage(new WriteCommand(), System.out);
        return 0;
    }
}
