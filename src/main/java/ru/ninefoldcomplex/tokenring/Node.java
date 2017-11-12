package ru.ninefoldcomplex.tokenring;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Node {
    private short serialNumber;
    private Queue<Frame> enqueuedFrames = new ConcurrentLinkedQueue<Frame>();

    public Node(short serialNumber) {
        this.serialNumber = serialNumber;
    }
}
