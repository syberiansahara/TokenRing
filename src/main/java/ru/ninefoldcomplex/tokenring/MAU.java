package ru.ninefoldcomplex.tokenring;

import java.util.Random;

public class MAU {
    public static short numberOfNodes = 1000;
    public static short numberOfFrames = 10;

    private static final Node[] nodes = new Node[numberOfNodes];
    private static final Frame[] frames = new Frame[numberOfFrames];

    {
        for (short i = 0; i < numberOfNodes; i++) {
            nodes[i] = new Node(i);
        }

        for (short i = 0; i < numberOfFrames; i++) {
            frames[i] = new Frame(i);
        }
    }

    public short getReceiverSerialNumber(short senderSerialNumber) {
        short receiverSerialNumber;
        do {
            receiverSerialNumber = (short) new Random().nextInt(numberOfNodes);
        } while (receiverSerialNumber == senderSerialNumber);
        return receiverSerialNumber;
    }

    public static void main(String[] args) {

    }


}
