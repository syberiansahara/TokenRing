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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ninefoldcomplex on 12.11.2017.
 */
public class Launcher {
    private final int targetDeliveredMessagesCount;
    private final double maximumTokenHoldingTime = Settings.maximumTokenHoldingTime;
    private final double launcherSleepTimeInterval = Settings.launcherSleepTimeInterval;
    private final double receiverSuccessProbability = Settings.receiverSuccessProbability;
    private final double[] tokenHoldingTimeMultiplierArray = Settings.tokenHoldingTimeMultiplierArray;

    private final short numberOfNodes;
    private final short numberOfFrames;
    private final double meanMessageGenerationInterval;

    private Node[] nodes;
    private Frame[] frames;
    private MessageGenerator messageGenerator;
    private List<Double> deliveryTimes;
    private AtomicInteger deliveredPayloadVolume;

    public Launcher(short numberOfNodes, short numberOfFrames, double meanMessageGenerationInterval) {
        this.numberOfNodes = numberOfNodes;
        this.numberOfFrames = numberOfFrames;
        this.meanMessageGenerationInterval = meanMessageGenerationInterval;
        this.targetDeliveredMessagesCount = Settings.targetDeliveredMessagesMultiplier * numberOfNodes;
    }

    public void executeBasicLaunch() throws Exception {
        runMainExecutionCycle(targetDeliveredMessagesCount, maximumTokenHoldingTime); //WarmUp
        reportBasicLaunch(runMainExecutionCycle(targetDeliveredMessagesCount, maximumTokenHoldingTime));
    }

    private void reportBasicLaunch(double executionTime) throws Exception {
        DescriptiveStatistics stats = new DescriptiveStatistics(deliveryTimes.stream().mapToDouble(d -> d).toArray());
        String report = Utils.getReportOnBasicLaunch(numberOfNodes, numberOfFrames,
                meanMessageGenerationInterval, executionTime,
                stats.getMean(), stats.getStandardDeviation());
        System.out.println("Basic launch: " + report);
        Path logFile = Settings.logRoot.resolve(numberOfNodes + "-" + numberOfFrames + ".txt");

        try (BufferedWriter bw = Files.newBufferedWriter(logFile,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            bw.write(report);
            bw.flush();
        }
    }

    public void executeTHTLaunches() throws Exception {
        double meanFrameTransmissionTime = getMeanFrameTransmissionTime();
        for (double THTMultiplier : tokenHoldingTimeMultiplierArray) {
            executeTHTLaunch(meanFrameTransmissionTime, THTMultiplier);
        }
    }

    public double getMeanFrameTransmissionTime() throws Exception {
        return Arrays.stream(Files.readAllLines(Settings.logRoot.resolve(numberOfNodes + "-" + numberOfFrames + "-" +
                meanMessageGenerationInterval + ".basic.txt")).get(0).split(","))
                .map(String::trim)
                .map(Double::parseDouble)
                .toArray(Double[]::new)[4];
    }

    public void executeTHTLaunch(double meanFrameTransmissionTime, double THTMultiplier) throws Exception {
        reportTHTLaunch(runMainExecutionCycle(targetDeliveredMessagesCount, meanFrameTransmissionTime * THTMultiplier), meanFrameTransmissionTime, THTMultiplier);
    }

    private void reportTHTLaunch(double executionTime, double meanFrameTransmissionTime, double THTMultiplier) throws Exception {
        DescriptiveStatistics stats = new DescriptiveStatistics(deliveryTimes.stream().mapToDouble(d -> d).toArray());

        String report = Utils.getReportOnLaunchWithTHT(numberOfNodes, numberOfFrames,
                meanMessageGenerationInterval, executionTime,
                stats.getMean(), stats.getStandardDeviation(),
                meanFrameTransmissionTime, THTMultiplier);
        System.out.println("THT launch: " + report);
        Path logFile = Settings.logRoot.resolve(numberOfNodes + "-" + numberOfFrames + "-" +
                meanMessageGenerationInterval + ".THT.txt");

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

        messageGenerator = new MessageGenerator(nodes, meanMessageGenerationInterval);
    }

    private double runMainExecutionCycle(int targetDeliveredMessagesCount, double tokenHoldingTime) throws InterruptedException {
        initializeLaunch(tokenHoldingTime);

        double fixedTime = Utils.getTimeInSeconds();

        messageGenerator.start();

        for (short i = 0; i < numberOfNodes; i++) {
            nodes[i].start();
        }

        while (deliveryTimes.size() < targetDeliveredMessagesCount) {
            Thread.sleep((long) launcherSleepTimeInterval * 1000);
        }

        for (short i = 0; i < numberOfNodes; i++) {
            messageGenerator.stop();
            nodes[i].stop();
        }

        return Utils.getTimeInSeconds() - fixedTime;
    }
}

