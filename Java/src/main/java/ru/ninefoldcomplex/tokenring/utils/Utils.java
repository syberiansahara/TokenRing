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

    static  {
        initialTime = System.nanoTime()/1000000000.0;
        decimalFormat.setMaximumFractionDigits(6);
    }

    public static double getTimeInSeconds() {
        return System.nanoTime()/1000000000.0 - initialTime;
    }

    public static String getTimeInSecondsForReport() {
        return decimalFormat.format(System.nanoTime()/1000000000.0 - initialTime);
    }

    public static String getReportOnLaunchWithTHT(short numberOfNodes, short numberOfFrames,
                                                  double meanMessageTimeInterval, double executionTime,
                                                  double mean, double std,
                                                  double meanFrameTransmissionTime, double THTMultiplier) {
        return getBasicReport(numberOfNodes, numberOfFrames, meanMessageTimeInterval, executionTime, mean, std) + ", " +
                decimalFormat.format(meanFrameTransmissionTime) + ", " +
                THTMultiplier +
                "\r\n";
    }

    public static String getReportOnBasicLaunch(short numberOfNodes, short numberOfFrames,
                                                double meanMessageTimeInterval, double executionTime,
                                                double mean, double std) {
        return getBasicReport(numberOfNodes, numberOfFrames, meanMessageTimeInterval, executionTime, mean, std) +
                "\r\n";
    }

    private static String getBasicReport(short numberOfNodes, short numberOfFrames,
                                        double meanMessageTimeInterval, double executionTime,
                                        double mean, double std) {
        return numberOfNodes + ", " +
                numberOfFrames + ", " +
                meanMessageTimeInterval + ", " +
                decimalFormat.format(executionTime) + ", " +
                decimalFormat.format(mean) + ", " +
                decimalFormat.format(std) + ", " +
                decimalFormat.format(Settings.targetDeliveredMessagesCount / executionTime);
    }
}
