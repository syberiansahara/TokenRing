package ru.ninefoldcomplex.tokenring.launchers;

/**
 * Created by ninefoldcomplex on 19.11.2017.
 */
public class MasterLauncher {

    private static short[] numberOfNodesArray = {2, 5, 10, 20};
    private static short[] numberOfFramesArray = {1, 2, 5, 10};
    private static double[] meanMessageTimeIntervalArray = {0.1, 0.5, 1.0, 2.0};
    private static double[] tokenHoldingTimeArray = {0, 0.1, 1.0, 10, 20, 100};
    private static double[] receiverSuccessProbabilityArray = {0.1, 0.2, 0.5, 0.9, 1.0};

    public static void main(String[] args) {
        for (short numberOfNodes : numberOfNodesArray) {
            for (short numberOfFrames : numberOfFramesArray) {
                for (double meanMessageTimeInterval : meanMessageTimeIntervalArray) {
                    for (double tokenHoldingTime : tokenHoldingTimeArray) {
                        for (double receiverSuccessProbability : receiverSuccessProbabilityArray) {
                            Launcher launcher = new Launcher(numberOfNodes, numberOfFrames,
                                    meanMessageTimeInterval, tokenHoldingTime, receiverSuccessProbability);
                            launcher.run();
                        }
                    }
                }
            }
        }
    }
}
