package ru.ninefoldcomplex.tokenring.threads;

import ru.ninefoldcomplex.tokenring.entities.Frame;
import ru.ninefoldcomplex.tokenring.entities.Message;
import ru.ninefoldcomplex.tokenring.utils.Utils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ninefoldcomplex on 12.11.2017.
 */
public class Node extends Thread {
    private final short nodeSerialNumber;
    private Node nextNode;
    private Queue<Frame> enqueuedFrames = new ConcurrentLinkedQueue<Frame>();
    private Queue<Message> pendingMessages = new ConcurrentLinkedQueue<Message>();
    private final double tokenHoldingTime;
    private List<Double> deliveryTimes;
    private AtomicInteger deliveredPayloadVolume;

    public Node(short nodeSerialNumber, double tokenHoldingTime, List<Double> deliveryTimes, AtomicInteger deliveredPayloadVolume) {
        this.nodeSerialNumber = nodeSerialNumber;
        this.tokenHoldingTime = tokenHoldingTime;
        this.deliveryTimes = deliveryTimes;
        this.deliveredPayloadVolume = deliveredPayloadVolume;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public void enqueueFrame(Frame frame) {
        enqueuedFrames.add(frame);
    }

    public void enqueueMessage(Message message) {
        pendingMessages.add(message);
    }

    public void run() {
        try {
            while (true) {
                if (! enqueuedFrames.isEmpty()) {
//                    Thread.sleep(1000);
                    handleTheFirstFrameInTheQueue();
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    public void handleTheFirstFrameInTheQueue() {
        Frame currentFrame = enqueuedFrames.remove();

        printReport(currentFrame);

        if (currentFrame.isToken()) {
            if (IHaveAPendingMessage()) {
                currentFrame.sendMessage(pendingMessages.remove());
            }
        } else {
            if (IAmTheReceiver(currentFrame)) {
                handleIncomingMessage(currentFrame);
            } else if (IAmTheSender(currentFrame)) {
                if (currentFrame.messageHasBeenDelivered()) {
                    currentFrame.releaseToken();
                } else if (currentFrame.messageNotYetDelivered()) {
                    handleUndeliveredMessage(currentFrame);
                }
            }
        }

        forwardFrame(currentFrame);
    }

    private void printReport(Frame currentFrame) {
        if (currentFrame.isToken()) {
            System.out.println("Time: " + Utils.getTimeInSecondsForReport() +
                            ", Node: " + nodeSerialNumber +
                            ", Frame: " + currentFrame.getFrameSerialNumber() +
                            ", IsToken: " + currentFrame.isToken()
            );
        } else {
            System.out.println("Time: " + Utils.getTimeInSecondsForReport() +
                    ", Node: " + nodeSerialNumber +
                    ", Frame: " + currentFrame.getFrameSerialNumber() +
                    ", IsToken: " + currentFrame.isToken() +
                    ", Receiver: " + currentFrame.getReceiver() +
                    ", Message Delivered: " + currentFrame.messageHasBeenDelivered()
            );
        }
    }

    private void handleIncomingMessage(Frame currentFrame) {
        //TODO periodic failure
        currentFrame.markMessageAsDelivered();
        double deliveryTime = Utils.getTimeInSeconds() - currentFrame.getSendTime();
        System.out.println("Message delivered in " +
                 deliveryTime + " seconds");
        deliveryTimes.add(deliveryTime);
        deliveredPayloadVolume.addAndGet(currentFrame.getPayloadVolume());
    }

    private void handleUndeliveredMessage(Frame currentFrame) {
        if (Utils.getTimeInSeconds() - currentFrame.getSendTime() > tokenHoldingTime) {
            currentFrame.releaseToken();
        }
    }

    private boolean IHaveAPendingMessage() {
        return ! pendingMessages.isEmpty();
    }

    private void forwardFrame(Frame currentFrame) {
        nextNode.enqueueFrame(currentFrame);
    }


    public boolean IAmTheSender(Frame currentFrame) {
        return nodeSerialNumber == currentFrame.getSender();
    }

    public boolean IAmTheReceiver(Frame currentFrame) {
        return nodeSerialNumber == currentFrame.getReceiver();
    }
}
