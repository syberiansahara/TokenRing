package ru.ninefoldcomplex.tokenring.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by ninefoldcomplex on 17.11.2017.
 */
public class Settings {
    public static int targetDeliveredMessagesMultiplier = 10;
    public static double maximumTokenHoldingTime = Double.MAX_VALUE;
    public static double launcherSleepTimeInterval = 0.0;
    public static double receiverSuccessProbability = 0.75;
    public static boolean debugModeIsOn = false;
    public static boolean weakDebugModeIsOn = false;
    public static Path logRoot = (Paths.get("")).toAbsolutePath().getParent().resolve("Calculations").resolve("THT");

    public static double[] tokenHoldingTimeMultiplierArray = {0.7, 1.0, 1.5, 5.0, 10, 15, 20};
}
