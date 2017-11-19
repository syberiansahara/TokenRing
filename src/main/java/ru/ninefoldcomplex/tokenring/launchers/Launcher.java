package ru.ninefoldcomplex.tokenring.launchers;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import ru.ninefoldcomplex.tokenring.entities.Frame;
import ru.ninefoldcomplex.tokenring.threads.MessageGenerator;
import ru.ninefoldcomplex.tokenring.threads.Node;
import ru.ninefoldcomplex.tokenring.utils.Settings;
import ru.ninefoldcomplex.tokenring.utils.Utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ninefoldcomplex on 12.11.2017.
 */
public class Launcher {
    private final double runInterval = Settings.runInterval;
    private final double launcherSleepTimeInterval = Settings.launcherSleepTimeInterval;

    private final short numberOfNodes;
    private final short numberOfFrames;
    private final double meanMessageTimeInterval;
    private final double tokenHoldingTime;
    private final double receiverSuccessProbability;

    private Node[] nodes;
    private Frame[] frames;
    private MessageGenerator messageGenerator;
    public List<Double> deliveryTimes = new CopyOnWriteArrayList<Double>();
    public AtomicInteger deliveredPayloadVolume = new AtomicInteger();

    public Launcher(short numberOfNodes, short numberOfFrames,
                    double meanMessageTimeInterval, double tokenHoldingTime, double receiverSuccessProbability) {
        this.numberOfNodes = numberOfNodes;
        this.numberOfFrames = numberOfFrames;
        this.meanMessageTimeInterval = meanMessageTimeInterval;
        this.tokenHoldingTime = tokenHoldingTime;
        this.receiverSuccessProbability = receiverSuccessProbability;
    }

    public void run() {
        try {
            initializeThreads();
            runMainExecutionCycle();
            analyzeLatency();
            analyzeThroughput();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void initializeThreads() {
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

    private void runMainExecutionCycle() throws InterruptedException {
        double stopTime = Utils.getTimeInSeconds() + runInterval;

        messageGenerator.start();

        for (short i = 0; i < numberOfNodes; i++) {
            nodes[i].start();
        }

        while (Utils.getTimeInSeconds() < stopTime) {
            Thread.sleep((long) launcherSleepTimeInterval * 1000);
        }

        for (short i = 0; i < numberOfNodes; i++) {
            messageGenerator.stop();
            nodes[i].stop();
        }
    }

    private void analyzeLatency() {
//        System.out.println(deliveryTimes);`
        DescriptiveStatistics stats = new DescriptiveStatistics();

        for( int i = 0; i < deliveryTimes.size(); i++) {
            stats.addValue(deliveryTimes.get(i));
        }

        double mean = stats.getMean();
        double std = stats.getStandardDeviation();

        System.out.println(mean);
        System.out.println(std);
        System.out.println(deliveryTimes.size());
    }

    private void analyzeThroughput() {
        System.out.println(deliveredPayloadVolume.get() / (3*1024.0));
    }
}
