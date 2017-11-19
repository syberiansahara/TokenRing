package ru.ninefoldcomplex.tokenring.utils;

import java.text.DecimalFormat;

/**
 * Created by ninefoldcomplex on 16.11.2017.
 */
public class Utils {
    private static double initialTime;
    private static DecimalFormat decimalFormatForTime = new DecimalFormat();
    private static DecimalFormat decimalFormatForMetrics = new DecimalFormat();

    static  {
        initialTime = System.nanoTime()/1000000000.0;
        decimalFormatForTime.setMaximumFractionDigits(6);
        decimalFormatForTime.setMaximumFractionDigits(4);
    }

    public static double getTimeInSeconds() {
        return System.nanoTime()/1000000000.0 - initialTime;
    }

    public static String getTimeInSecondsForReport() {
        return decimalFormatForTime.format(System.nanoTime()/1000000000.0 - initialTime);
    }

    public static String getReport(short numberOfNodes, short numberOfFrames,
                                   double meanMessageTimeInterval, double tokenHoldingTime, double receiverSuccessProbability, double mean, double std, int throughput) {
        return numberOfNodes + ", " +
                numberOfFrames + ", " +
                meanMessageTimeInterval + ", " +
                tokenHoldingTime + ", " +
                receiverSuccessProbability + ", " +
                decimalFormatForMetrics.format(mean) + " +- " +
                decimalFormatForMetrics.format(std) + ", "
                + throughput
                + "\r\n";
    }
}
