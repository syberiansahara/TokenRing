package ru.ninefoldcomplex.tokenring.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by ninefoldcomplex on 17.11.2017.
 */
public class Settings {
    public static double runInterval = 200.0;
    public static double trialRunInterval = runInterval / 10;
    public static double launcherSleepTimeInterval = 1.0;
    public static double receiverSuccessProbability = 0.7;
    public static boolean debugModeIsOn = false;
    public static boolean weakDebugModeIsOn = true;
    public static Path logFile = (Paths.get("")).toAbsolutePath().getParent().resolve("Logs").resolve("Log.txt");
}
