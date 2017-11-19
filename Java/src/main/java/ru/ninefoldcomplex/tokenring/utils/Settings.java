package ru.ninefoldcomplex.tokenring.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by ninefoldcomplex on 17.11.2017.
 */
public class Settings {
    public static double runInterval = 30.0;
    public static double launcherSleepTimeInterval = 1.0;
    public static boolean debugModeIsOn = false;
    public static boolean weakDebugModeIsOn = false;
    public static Path logFile = (Paths.get("")).resolve("logs").resolve("Log.txt").toAbsolutePath();
}
