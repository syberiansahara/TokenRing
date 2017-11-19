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
    private final double runInterval = Settings.runInterval;
    private final double trialRunInterval = Settings.trialRunInterval;
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
        runMainExecutionCycle(trialRunInterval, Double.MAX_VALUE);
        return new DescriptiveStatistics(deliveryTimes.stream().mapToDouble(d -> d).toArray()).getMean();
    }

    public void launchSystemWithThisTokenHoldingTime(double tokenHoldingTime) throws Exception {
        runMainExecutionCycle(runInterval, tokenHoldingTime);
        analyzeAndLogResults(tokenHoldingTime);
    }


    private void initializeLaunch(double receiverSuccessProbability, double tokenHoldingTime) {
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

    private void runMainExecutionCycle(double runInterval, double tokenHoldingTime) throws InterruptedException {
        initializeLaunch(receiverSuccessProbability, tokenHoldingTime);

        messageGenerator.start();

        for (short i = 0; i < numberOfNodes; i++) {
            nodes[i].start();
        }

        double stopTime = Utils.getTimeInSeconds() + runInterval;

        while (Utils.getTimeInSeconds() < stopTime) {
            Thread.sleep((long) launcherSleepTimeInterval * 1000);
        }

        for (short i = 0; i < numberOfNodes; i++) {
            messageGenerator.stop();
            nodes[i].stop();
        }
    }

    private void analyzeAndLogResults(double tokenHoldingTime) throws IOException {
        //todo refactoring
        DescriptiveStatistics stats = new DescriptiveStatistics(deliveryTimes.stream().mapToDouble(d -> d).toArray());

        String report = Utils.getReport(numberOfNodes, numberOfFrames,
                meanMessageTimeInterval, tokenHoldingTime, receiverSuccessProbability, stats.getMean(), stats.getStandardDeviation(), deliveryTimes.size());

        System.out.println(report);

        try (BufferedWriter bw = Files.newBufferedWriter(Settings.logFile,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
            bw.write(report);
            bw.flush();
        }
    }
}
