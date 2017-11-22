package ru.ninefoldcomplex.tokenring.threads;

import ru.ninefoldcomplex.tokenring.entities.Message;
import ru.ninefoldcomplex.tokenring.utils.Settings;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ninefoldcomplex on 17.11.2017.
 */
public class MessageGenerator extends Thread {
    private Node[] nodes;
    private short numberOfNodes;
    private final double meanMessageGenerationInterval;

    public MessageGenerator(Node[] nodes, double meanMessageGenerationInterval) {
        this.nodes = nodes;
        this.numberOfNodes = (short) nodes.length;
        this.meanMessageGenerationInterval = meanMessageGenerationInterval;
    }

    public void run() {
        try {
            while (true) {
                short nodeSerialNumber = (short) ThreadLocalRandom.current().nextInt(0, numberOfNodes);
                enqueueAPendingMessageOnThisNode(nodeSerialNumber);
                long nextSleepInterval = getNextSleepInterval();
                if (Settings.debugModeIsOn) System.out.println("Enqueued a message in " + nextSleepInterval/1000.0 +
                "s on " + nodeSerialNumber + " node");
                Thread.sleep(nextSleepInterval);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private long getNextSleepInterval() {
        return new Double(Math.log(1 - (new Random().nextDouble()))*( - 1000.0 * meanMessageGenerationInterval)).longValue();
    }

    private void enqueueAPendingMessageOnThisNode(short serialNumber) {
        nodes[serialNumber].enqueueMessage(new Message(serialNumber, getReceiverSerialNumber(serialNumber)));
    }

    public short getReceiverSerialNumber(short senderSerialNumber) {
        short receiverSerialNumber;
        do {
            receiverSerialNumber = (short) new Random().nextInt(numberOfNodes);
        } while (receiverSerialNumber == senderSerialNumber);
        return receiverSerialNumber;
    }
}
