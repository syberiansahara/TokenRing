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

    public static String getReportOnLaunchCompletion(short numberOfNodes, short numberOfFrames,
                                                     double meanMessageTimeInterval, double executionTime,
                                                     double meanFrameTransmissionTime, double THTMultiplier,
                                                     double mean, double std) {
        return numberOfNodes + ", " +
                numberOfFrames + ", " +
                meanMessageTimeInterval + ", " +
                decimalFormat.format(executionTime) + ", " +
                decimalFormat.format(meanFrameTransmissionTime) + ", " +
                decimalFormat.format(THTMultiplier) + ", " +
                decimalFormat.format(mean) + ", " +
                decimalFormat.format(std) +
                "\r\n";
    }

    public static String getReportOnTrialLaunchCompletion(short numberOfNodes, short numberOfFrames,
                                                     double meanMessageTimeInterval, double executionTime) {
        return numberOfNodes + ", " +
                numberOfFrames + ", " +
                meanMessageTimeInterval + ", " +
                decimalFormat.format(executionTime) +
                "\r\n";
    }
}
