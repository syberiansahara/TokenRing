package ru.ninefoldcomplex.tokenring;

import ru.ninefoldcomplex.tokenring.entities.Frame;
import ru.ninefoldcomplex.tokenring.entities.Message;
import ru.ninefoldcomplex.tokenring.utils.Utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ninefoldcomplex on 12.11.2017.
 */
public class MAU {
    public static short numberOfNodes = 10;
    public static short numberOfFrames = 1;

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

    public static short getReceiverSerialNumber(short senderSerialNumber) {
        short receiverSerialNumber;
        do {
            receiverSerialNumber = (short) new Random().nextInt(numberOfNodes);
        } while (receiverSerialNumber == senderSerialNumber);
        return receiverSerialNumber;
    }

    public static void main(String[] args) throws Exception {
        run();
    }

    private static void run() throws Exception{
        short n = 1;
        enqueueAPendingMessageOnThisNode(n);

        double stopTime = Utils.getTimeInSeconds() + 30.0;

        for (short i = 0; i < numberOfNodes; i++) {
            nodes[i].start();
        }

        while(Utils.getTimeInSeconds() < stopTime) {
        }

        for (short i = 0; i < numberOfNodes; i++) {
            nodes[i].stop();
        }

//        double fixedTimeOld = Utils.getTimeInSeconds();
//        double fixedTime;
//        for (int i = 0; i<10; i++) {
//            Thread.sleep(2000);
//            fixedTime = Utils.getTimeInSeconds();
//            System.out.println(fixedTime - fixedTimeOld);
//            System.out.println(fixedTime);
//            fixedTimeOld = fixedTime;
    }

    private static void enqueueAPendingMessageOnThisNode(short serialNumber) {
        nodes[serialNumber].enqueueMessage(new Message(serialNumber, getReceiverSerialNumber(serialNumber)));
    }


}
