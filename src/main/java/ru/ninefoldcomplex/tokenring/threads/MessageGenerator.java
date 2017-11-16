package ru.ninefoldcomplex.tokenring.threads;

import ru.ninefoldcomplex.tokenring.entities.Message;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ninefoldcomplex on 17.11.2017.
 */
public class MessageGenerator extends Thread {
    private double meanSleepInterval = 5000;
    private Node[] nodes;
    private short numberOfNodes;

    public MessageGenerator(Node[] nodes) {
        this.nodes = nodes;
        this.numberOfNodes = (short) nodes.length;
    }

    public void run() {
        try {
            while (true) {
                long nextSleepInterval = getNextSleepInterval();
                short nodeSerialNumber = (short) ThreadLocalRandom.current().nextInt(0, numberOfNodes);
                System.out.println("Enqueued a message in " + nextSleepInterval/1000.0 +
                "s on " + nodeSerialNumber + " node");
                enqueueAPendingMessageOnThisNode(nodeSerialNumber);
                Thread.sleep(nextSleepInterval);
            }
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    private long getNextSleepInterval() {
        return new Double(Math.log(1 - (new Random().nextDouble()))*(- meanSleepInterval))
                .longValue();
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
