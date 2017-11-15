package ru.ninefoldcomplex.tokenring.utils;

import java.text.DecimalFormat;

/**
 * Created by ninefoldcomplex on 16.11.2017.
 */
public class Utils {
    private static double initialTime;
    private static DecimalFormat df = new DecimalFormat();

    static  {
        initialTime = System.nanoTime()/1000000000.0;
        df.setMaximumFractionDigits(6);
    }

    public static double getTimeInSeconds() {
        return System.nanoTime()/1000000000.0 - initialTime;
    }

    public static String getTimeInSecondsForReport() {
        return df.format(System.nanoTime()/1000000000.0 - initialTime);
    }
}
