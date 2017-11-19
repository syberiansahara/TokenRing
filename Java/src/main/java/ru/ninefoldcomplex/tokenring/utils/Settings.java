package ru.ninefoldcomplex.tokenring.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by ninefoldcomplex on 17.11.2017.
 */
public class Settings {
    public static int targetCyclesCount = 50;
    public static int trialTargetCyclesCount = 5;

    public static double maximumTokenHoldingTime = Double.MAX_VALUE;
    public static double launcherSleepTimeInterval = 0.0;
    public static double receiverSuccessProbability = 0.7;
    public static boolean debugModeIsOn = false;
    public static boolean weakDebugModeIsOn = false;
    public static Path logFile = (Paths.get("")).toAbsolutePath().getParent().resolve("Logs").resolve("Log.txt");
}
