package ru.ninefoldcomplex.tokenring.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by ninefoldcomplex on 16.11.2017.
 */
public class Utils {
    private static double initialTime;
    private static DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMAN);

    static  {
        initialTime = System.nanoTime()/1000000000.0;
        decimalFormat.setMaximumFractionDigits(4);
        decimalFormat.setGroupingUsed(true);
    }

    public static double getTimeInSeconds() {
        return System.nanoTime()/1000000000.0 - initialTime;
    }

    public static String getTimeInSecondsForReport() {
        return decimalFormat.format(System.nanoTime()/1000000000.0 - initialTime);
    }

    public static String getReportOnLaunchCompletion(short numberOfNodes, short numberOfFrames,
                                                     double meanMessageTimeInterval, double tokenHoldingTime, double mean, double std, double executionTime) {
        return numberOfNodes + ", " +
                numberOfFrames + ", " +
                meanMessageTimeInterval + ", " +
                decimalFormat.format(tokenHoldingTime) + ", " +
                decimalFormat.format(mean) + " +- " +
                decimalFormat.format(std) + ", "
                + decimalFormat.format(executionTime)
                + "\r\n";
    }

    public static String getReportBeforeLaunchStart(short numberOfNodes, short numberOfFrames,
                                                     double meanMessageTimeInterval, double tokenHoldingTime) {
        if (tokenHoldingTime < Settings.maximumTokenHoldingTime)
            return "\r\nMain Execution: " +
                numberOfNodes + ", " +
                numberOfFrames + ", " +
                meanMessageTimeInterval + ", " +
                decimalFormat.format(tokenHoldingTime);
        else
            return "\r\nTrial Execution: " +
                numberOfNodes + ", " +
                numberOfFrames + ", " +
                meanMessageTimeInterval;
    }
}
