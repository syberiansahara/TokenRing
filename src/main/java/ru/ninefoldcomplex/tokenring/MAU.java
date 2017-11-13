package ru.ninefoldcomplex.tokenring;

import ru.ninefoldcomplex.tokenring.Entity.Frame;
import ru.ninefoldcomplex.tokenring.Entity.Message;

import java.util.Random;

public class MAU {
    public static short numberOfNodes = 10;
    public static short numberOfFrames = 10;

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
        }
    }

    public static short getReceiverSerialNumber(short senderSerialNumber) {
        short receiverSerialNumber;
        do {
            receiverSerialNumber = (short) new Random().nextInt(numberOfNodes);
        } while (receiverSerialNumber == senderSerialNumber);
        return receiverSerialNumber;
    }

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        short n = 1;
        enqueueAPendingMessageOnThisNode(n);
    }

    private static void enqueueAPendingMessageOnThisNode(short serialNumber) {
        nodes[serialNumber].enqueueMessage(new Message(getReceiverSerialNumber(serialNumber)));
    }


}
