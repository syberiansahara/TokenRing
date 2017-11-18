package ru.ninefoldcomplex.tokenring.launchers;

/**
 * Created by ninefoldcomplex on 19.11.2017.
 */
public class MasterLauncher {

    private static short numberOfNodes = 10;
    private static short numberOfFrames = 1;
    private static double meanMessageTimeInterval = 1.0;
    private static double tokenHoldingTime = 10.0;
    private static double receiverFailureProbability = 0.5;

    public static void main(String[] args) {
        Launcher launcher = new Launcher(numberOfNodes, numberOfFrames,
                meanMessageTimeInterval, tokenHoldingTime, receiverFailureProbability);
        launcher.run();
    }
}
