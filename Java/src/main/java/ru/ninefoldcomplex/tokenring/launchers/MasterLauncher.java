package ru.ninefoldcomplex.tokenring.launchers;

/**
 * Created by ninefoldcomplex on 19.11.2017.
 */
public class MasterLauncher {
//    private static short[] numberOfNodesArray = {2, 5, 10, 20};
//    private static short[] numberOfFramesArray = {1, 2, 5, 10};
//    private static double[] meanMessageGenerationIntervalArray = {0.1, 0.5, 1.0, 2.0};

    private static short[] numberOfNodesArray = {5};
//    private static short[] numberOfNodesArray = {15, 25, 50, 100};
    private static double[] meanMessageGenerationIntervalArray = {0.001};

    public static void main(String[] args) throws Exception {
        for (short numberOfNodes : numberOfNodesArray) {
            int interval = numberOfNodes / 5;
//            for (short numberOfFrames : numberOfFramesArray) {
            for (int numberOfFrames = numberOfNodes; numberOfFrames > 0; numberOfFrames -= interval) {
                for (double meanMessageGenerationInterval : meanMessageGenerationIntervalArray) {
                    Launcher launcher = new Launcher(numberOfNodes, (short) numberOfFrames, meanMessageGenerationInterval);
                    launcher.executeBasicLaunch();
//                    launcher.executeTHTLaunches();
                }
            }
        }
    }
}
