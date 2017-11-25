package ru.ninefoldcomplex.tokenring.launchers;

/**
 * Created by ninefoldcomplex on 19.11.2017.
 */
public class MasterLauncher {

    private static short[] numberOfNodesArray = {65, 85};
    private static double[] meanMessageGenerationIntervalArray = {0.01};

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
