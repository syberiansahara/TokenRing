package ru.ninefoldcomplex.tokenring;

import ru.ninefoldcomplex.tokenring.entities.Frame;
import ru.ninefoldcomplex.tokenring.threads.MessageGenerator;
import ru.ninefoldcomplex.tokenring.threads.Node;
import ru.ninefoldcomplex.tokenring.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ninefoldcomplex on 12.11.2017.
 */
public class MAU {
    private static double runInterval = 30.0;
    private static int sleepTime = 1000;

    private static short numberOfNodes = 10;
    private static short numberOfFrames = 1;

    private static Node[] nodes = new Node[numberOfNodes];
    private static Frame[] frames = new Frame[numberOfFrames];

    static {
        for (short i = 0; i < numberOfNodes; i++) {
            nodes[i] = new Node(i);
        }

        for (short i = 0; i < numberOfNodes; i++) {
            nodes[i].setNextNode(nodes[(i+1)%numberOfNodes]);
        }

        for (short i = 0; i < numberOfFrames; i++) {
            frames[i] = new Frame(i);
            nodes[(short) ThreadLocalRandom.current().nextInt(0, numberOfNodes)].enqueueFrame(frames[i]);
        }
    }

    public static void main(String[] args) throws Exception {
        run();
    }

    private static void run() throws Exception{
        double stopTime = Utils.getTimeInSeconds() + runInterval;

        MessageGenerator messageGenerator = new MessageGenerator(nodes);
        messageGenerator.start();
        for (short i = 0; i < numberOfNodes; i++) {
            nodes[i].start();
        }

        while(Utils.getTimeInSeconds() < stopTime) {
            Thread.sleep(sleepTime);
        }

        for (short i = 0; i < numberOfNodes; i++) {
            messageGenerator.stop();
            nodes[i].stop();
        }
    }
}
