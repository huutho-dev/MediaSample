package com.htn.samplefragment;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by ThoNh on 2/27/2018.
 */

public class Utils {
    public static String convertMillisToSecond(long millis) {
       return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }
}
