package org.mongodb.utils;

import org.springframework.format.annotation.DurationFormat;
import org.springframework.format.datetime.standard.DurationFormatterUtils;

import java.time.Duration;

public interface DurationUtils {
    static String msToHuman(long ms){
        return DurationFormatterUtils.print(Duration.ofMillis(ms), DurationFormat.Style.COMPOSITE);
    }
}
