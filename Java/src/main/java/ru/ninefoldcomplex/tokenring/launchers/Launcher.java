package ru.ninefoldcomplex.tokenring.launchers;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import ru.ninefoldcomplex.tokenring.entities.Frame;
import ru.ninefoldcomplex.tokenring.threads.MessageGenerator;
import ru.ninefoldcomplex.tokenring.threads.Node;
import ru.ninefoldcomplex.tokenring.utils.Settings;
import ru.ninefoldcomplex.tokenring.utils.Utils;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private final double[] tokenHoldingTimeMultiplierArray = Settings.tokenHoldingTimeMultiplierArray;

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

    public void run() throws Exception {
        double meanFrameTransmissionTime = getMeanFrameTransmissionTime();
        for (double THTMultiplier : tokenHoldingTimeMultiplierArray) {
            launchSystemWithThisTokenHoldingTime(meanFrameTransmissionTime, THTMultiplier);
        }
    }

    public double getMeanFrameTransmissionTime() throws Exception {
        reportTrialLaunch(runMainExecutionCycle(trialTargetCyclesCount, maximumTokenHoldingTime));
        return new DescriptiveStatistics(deliveryTimes.stream().mapToDouble(d -> d).toArray()).getMean();
    }

    private void reportTrialLaunch(double executionTime) throws Exception {
        String report = Utils.getReportOnTrialLaunchCompletion(numberOfNodes, numberOfFrames,
                meanMessageTimeInterval, executionTime);
        System.out.println("Trial launch: " + report);
        Path logFile = Settings.logRoot.resolve(numberOfNodes + "-" + numberOfFrames + "-" +
                meanMessageTimeInterval + ".trial.txt");

        try (BufferedWriter bw = Files.newBufferedWriter(logFile,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
            bw.write(report);
            bw.flush();
        }
    }

    public void launchSystemWithThisTokenHoldingTime(double meanFrameTransmissionTime, double THTMultiplier) throws Exception {
        double executionTime = runMainExecutionCycle(targetCyclesCount, meanFrameTransmissionTime * THTMultiplier);
        reportLaunch(executionTime, meanFrameTransmissionTime, THTMultiplier);
    }

    private void reportLaunch(double executionTime, double meanFrameTransmissionTime, double THTMultiplier) throws Exception {
        DescriptiveStatistics stats = new DescriptiveStatistics(deliveryTimes.stream().mapToDouble(d -> d).toArray());

        String report = Utils.getReportOnLaunchCompletion(numberOfNodes, numberOfFrames,
                meanMessageTimeInterval, executionTime, meanFrameTransmissionTime, THTMultiplier,
                stats.getMean(), stats.getStandardDeviation());
        System.out.println("Main launch: " + report);
        Path logFile = Settings.logRoot.resolve(numberOfNodes + "-" + numberOfFrames + "-" +
                meanMessageTimeInterval + "-" + THTMultiplier + ".txt");

        try (BufferedWriter bw = Files.newBufferedWriter(logFile,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
            bw.write(report);
            bw.flush();
        }
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

    private double runMainExecutionCycle(int targetCyclesCount, double tokenHoldingTime) throws InterruptedException {
        initializeLaunch(tokenHoldingTime);

        double fixedTime = Utils.getTimeInSeconds();

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

        return Utils.getTimeInSeconds() - fixedTime;
    }
}

