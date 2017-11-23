package ru.ninefoldcomplex.tokenring.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by ninefoldcomplex on 16.11.2017.
 */
public class Utils {
    private static double initialTime;
    private static DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
    private static DecimalFormat shortDecimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);

    static  {
        initialTime = System.nanoTime()/1000000000.0;
        decimalFormat.setMaximumFractionDigits(6);
        shortDecimalFormat.setMaximumFractionDigits(1);
    }

    public static double getTimeInSeconds() {
        return System.nanoTime()/1000000000.0 - initialTime;
    }

    public static String getTimeInSecondsForReport() {
        return decimalFormat.format(System.nanoTime()/1000000000.0 - initialTime);
    }

    public static String getReportOnLaunchWithTHT(short numberOfNodes, short numberOfFrames,
                                                  double meanMessageGenerationInterval, double executionTime,
                                                  double mean, double std,
                                                  double meanFrameTransmissionTime, double THTMultiplier) {
        return getBasicReport(numberOfNodes, numberOfFrames, meanMessageGenerationInterval, executionTime, mean, std) + " " +
                decimalFormat.format(meanFrameTransmissionTime) + " " +
                THTMultiplier +
                "\r\n";
    }

    public static String getReportOnBasicLaunch(short numberOfNodes, short numberOfFrames,
                                                double meanMessageGenerationInterval, double executionTime,
                                                double mean, double std) {
        return getBasicReport(numberOfNodes, numberOfFrames, meanMessageGenerationInterval, executionTime, mean, std) +
                "\r\n";
    }

    private static String getBasicReport(short numberOfNodes, short numberOfFrames,
                                        double meanMessageGenerationInterval, double executionTime,
                                        double mean, double std) {
        boolean overloaded = mean > numberOfFrames * meanMessageGenerationInterval;
        return numberOfNodes + " " +
                numberOfFrames + " " +
                meanMessageGenerationInterval + " " +
                decimalFormat.format(executionTime) + " " +
                decimalFormat.format(mean) + " " +
                decimalFormat.format(std) + " " +
                decimalFormat.format(Settings.targetDeliveredMessagesMultiplier * numberOfNodes / executionTime) + " " +
                (overloaded ? "overloaded " + shortDecimalFormat.format(numberOfFrames * 1.0 / numberOfNodes)
                        : "underloaded " + shortDecimalFormat.format(mean / (meanMessageGenerationInterval * numberOfNodes)));
    }
}
