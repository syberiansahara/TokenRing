package ru.ninefoldcomplex.tokenring.launchers;

/**
 * Created by ninefoldcomplex on 19.11.2017.
 */
public class MasterLauncher {
//    private static short[] numberOfNodesArray = {2, 5, 10, 20};
//    private static short[] numberOfFramesArray = {1, 2, 5, 10};
//    private static double[] meanMessageTimeIntervalArray = {0.1, 0.5, 1.0, 2.0};

    private static short[] numberOfNodesArray = {5, 10};
    private static short[] numberOfFramesArray = {2, 5};
    private static double[] meanMessageTimeIntervalArray = {0.1, 1.0};

    public static void main(String[] args) throws Exception {
        for (short numberOfNodes : numberOfNodesArray) {
            for (short numberOfFrames : numberOfFramesArray) {
                for (double meanMessageTimeInterval : meanMessageTimeIntervalArray) {
                    Launcher launcher = new Launcher(numberOfNodes, numberOfFrames, meanMessageTimeInterval);
                    double meanFrameTransmissionTime = launcher.getMeanFrameTransmissionTime();
                    double[] tokenHoldingTimeArray = {0,
                            meanFrameTransmissionTime * 0.5,
                            meanFrameTransmissionTime * 1.2,
                            meanFrameTransmissionTime * 10};
                    for (double tokenHoldingTime : tokenHoldingTimeArray) {
                        launcher.launchSystemWithThisTokenHoldingTime(tokenHoldingTime);
                    }
                }
            }
        }
    }
}
