package ru.ninefoldcomplex.tokenring;

import ru.ninefoldcomplex.tokenring.Entity.Frame;
import ru.ninefoldcomplex.tokenring.Entity.Message;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Node extends Thread {
    private final short nodeSerialNumber;
    private Node nextNode;
    private Queue<Frame> enqueuedFrames = new ConcurrentLinkedQueue<Frame>();
    private Queue<Message> pendingMessages = new ConcurrentLinkedQueue<Message>();

    public Node(short nodeSerialNumber) {
        this.nodeSerialNumber = nodeSerialNumber;
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

    public void handleTheFirstFrameInTheQueue() {
        Frame currentFrame = enqueuedFrames.element();
        if (currentFrame.isEmpty()) {
            if (! pendingMessages.isEmpty()) {
                currentFrame.seize(nodeSerialNumber, pendingMessages.element());
                pendingMessages.remove();
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

        if (currentFrame.isEmpty()) {
            if (IHaveAPendingMessage()) {
                currentFrame.seize(nodeSerialNumber, pendingMessages.remove());
            }
        } else {
            if (IAmTheReceiver(currentFrame)) {


            } else if (IAmTheSender(currentFrame)) {
                if (currentFrame.messageNotYetDelivered()) {

                } else if (currentFrame.messageDelivered()) {
                    currentFrame.clearMessage();
                }
            }
        }
        forwardFrame(currentFrame);
    }

    private boolean IHaveAPendingMessage() {
        return ! pendingMessages.isEmpty();
    }

    private void forwardFrame(Frame currentFrame) {
        nextNode.enqueueFrame(enqueuedFrames.remove());
    }


    public boolean IAmTheSender(Frame currentFrame) {
        return nodeSerialNumber == currentFrame.getSender();
    }

    public boolean IAmTheReceiver(Frame currentFrame) {
        return nodeSerialNumber == currentFrame.getReceiver();
    }
}
