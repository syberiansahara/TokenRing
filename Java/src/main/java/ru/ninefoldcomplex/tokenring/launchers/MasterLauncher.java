package ru.ninefoldcomplex.tokenring.launchers;

/**
 * Created by ninefoldcomplex on 19.11.2017.
 */
public class MasterLauncher {
//    private static short[] numberOfNodesArray = {2, 5, 10, 20};
//    private static short[] numberOfFramesArray = {1, 2, 5, 10};
//    private static double[] meanMessageGenerationIntervalArray = {0.1, 0.5, 1.0, 2.0};
private static short[] numberOfNodesArray = {15};

    private static short[] numberOfFramesArray = {15};
    private static double[] meanMessageGenerationIntervalArray = {0.5};

    public static void main(String[] args) throws Exception {
        for (short numberOfNodes : numberOfNodesArray) {
            for (short numberOfFrames : numberOfFramesArray) {
                for (double meanMessageGenerationInterval : meanMessageGenerationIntervalArray) {
                    Launcher launcher = new Launcher(numberOfNodes, numberOfFrames, meanMessageGenerationInterval);
                    launcher.executeBasicLaunch();
//                    launcher.executeTHTLaunches();
                }
            }
        }
    }
}
