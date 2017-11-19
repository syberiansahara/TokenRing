package ru.ninefoldcomplex.tokenring.launchers;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import ru.ninefoldcomplex.tokenring.entities.Frame;
import ru.ninefoldcomplex.tokenring.threads.MessageGenerator;
import ru.ninefoldcomplex.tokenring.threads.Node;
import ru.ninefoldcomplex.tokenring.utils.Settings;
import ru.ninefoldcomplex.tokenring.utils.Utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ninefoldcomplex on 12.11.2017.
 */
public class Launcher {
    private final int targetCyclesCount = Settings.targetCyclesCount;
    private final int trialTargetCyclesCount = Settings.trialTargetCyclesCount;

    private final double maximumTokenHoldingTime = Settings.maximumTokenHoldingTime;
    private final double launcherSleepTimeInterval = Settings.launcherSleepTimeInterval;
    private final double receiverSuccessProbability = Settings.receiverSuccessProbability;

    private final short numberOfNodes;
    private final short numberOfFrames;
    private final double meanMessageTimeInterval;

    private Node[] nodes;
    private Frame[] frames;
    private MessageGenerator messageGenerator;
    private List<Double> deliveryTimes;
    private AtomicInteger deliveredPayloadVolume;

    public Launcher(short numberOfNodes, short numberOfFrames, double meanMessageTimeInterval) {
        this.numberOfNodes = numberOfNodes;
        this.numberOfFrames = numberOfFrames;
        this.meanMessageTimeInterval = meanMessageTimeInterval;
    }

    public double getMeanFrameTransmissionTime() throws Exception {
        double executionTime = runMainExecutionCycleAndReport(trialTargetCyclesCount, maximumTokenHoldingTime);
        System.out.println(executionTime);
        return new DescriptiveStatistics(deliveryTimes.stream().mapToDouble(d -> d).toArray()).getMean();
    }

    public void launchSystemWithThisTokenHoldingTime(double tokenHoldingTime) throws Exception {
        double executionTime = runMainExecutionCycleAndReport(targetCyclesCount, tokenHoldingTime);
        analyzeAndLogResults(tokenHoldingTime, executionTime);
    }

    private void initializeLaunch(double tokenHoldingTime) {
        deliveryTimes = new CopyOnWriteArrayList<Double>();
        deliveredPayloadVolume = new AtomicInteger();

        nodes = new Node[numberOfNodes];
        frames = new Frame[numberOfFrames];

        for (short i = 0; i < numberOfNodes; i++) {
            nodes[i] = new Node(i, tokenHoldingTime, receiverSuccessProbability, deliveryTimes, deliveredPayloadVolume);
        }

        for (short i = 0; i < numberOfNodes; i++) {
            nodes[i].setNextNode(nodes[(i+1)%numberOfNodes]);
        }

        for (short i = 0; i < numberOfFrames; i++) {
            frames[i] = new Frame(i);
            nodes[(short) ThreadLocalRandom.current().nextInt(0, numberOfNodes)].enqueueFrame(frames[i]);
        }

        messageGenerator = new MessageGenerator(nodes, meanMessageTimeInterval);
    }

    private void runMainExecutionCycle(int targetCyclesCount, double tokenHoldingTime) throws InterruptedException {
        initializeLaunch(tokenHoldingTime);

        messageGenerator.start();

        for (short i = 0; i < numberOfNodes; i++) {
            nodes[i].start();
        }

        while (deliveryTimes.size() < targetCyclesCount) {
            Thread.sleep((long) launcherSleepTimeInterval * 1000);
        }

        for (short i = 0; i < numberOfNodes; i++) {
            messageGenerator.stop();
            nodes[i].stop();
        }
    }

    private double runMainExecutionCycleAndReport(int targetCyclesCount, double tokenHoldingTime) throws InterruptedException {
        System.out.println(Utils.getReportBeforeLaunchStart(numberOfNodes, numberOfFrames,
                meanMessageTimeInterval, tokenHoldingTime));
        double fixedTime = Utils.getTimeInSeconds();
        runMainExecutionCycle(targetCyclesCount, tokenHoldingTime);
        return Utils.getTimeInSeconds() - fixedTime;
    }

    private void analyzeAndLogResults(double tokenHoldingTime, double executionTime) throws IOException {
        //todo refactoring
        DescriptiveStatistics stats = new DescriptiveStatistics(deliveryTimes.stream().mapToDouble(d -> d).toArray());

        String report = Utils.getReportOnLaunchCompletion(numberOfNodes, numberOfFrames,
                meanMessageTimeInterval, tokenHoldingTime, stats.getMean(), stats.getStandardDeviation(), executionTime);

        System.out.println(report);

        try (BufferedWriter bw = Files.newBufferedWriter(Settings.logFile,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
            bw.write(report);
            bw.flush();
        }
    }
}
