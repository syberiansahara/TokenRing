package ru.ninefoldcomplex.tokenring;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Node {
    private short serialNumber;
    private Queue<Frame> enqueuedFrames = new ConcurrentLinkedQueue<Frame>();
    private Queue<Short> pendingReceivers = new ConcurrentLinkedQueue<Short>();

    public Node(short serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void enqueueFrame(Frame frame) {
        enqueuedFrames.add(frame);
    }

    public void enqueueReceiver(short receiverSerialNumber) {
        pendingReceivers.add(receiverSerialNumber);
    }

    public void handleTheFirstFrameInTheQueue() {
        Frame currentFrame = enqueuedFrames.element();
        if (currentFrame.frameIsEmpty()) {
            if (! pendingReceivers.isEmpty()) {
                currentFrame.seizeFrame(serialNumber, pendingReceivers.element());
                pendingReceivers.remove();
            }
        } else if (currentFrame.messageNotYetDelivered()) {
            if (IAmTheReceiver(currentFrame)) {
                currentFrame.releaseToken();
            } else if (IAmTheSender(currentFrame)) {
                throw new RuntimeException("Message went through the full cycle and was not delivered");
            }
        } else if (currentFrame.messageDelivered()) {
            if (IAmTheSender(currentFrame)) {
                currentFrame.clearMessage();
            }
        }
        enqueuedFrames.remove();
    }



    public boolean IAmTheSender(Frame currentFrame) {
        return serialNumber == currentFrame.getSender();
    }

    public boolean IAmTheReceiver(Frame currentFrame) {
        return serialNumber == currentFrame.getReceiver();
    }
}
