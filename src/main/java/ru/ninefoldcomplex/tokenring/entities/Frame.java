package ru.ninefoldcomplex.tokenring.entities;

import ru.ninefoldcomplex.tokenring.utils.Utils;

/**
 * Created by ninefoldcomplex on 12.11.2017.
 */
public class Frame {
    private final short frameSerialNumber;
    private Message message;
    private double fixedTime;

    public Frame(short frameSerialNumber) {
        this.frameSerialNumber = frameSerialNumber;
        fixTime();
    }

    public short getFrameSerialNumber() {
        return frameSerialNumber;
    }

    public void fixTime() {
        this.fixedTime = Utils.getTimeInSeconds();
    }

    public boolean isToken() {
        return message == null;
    }

    public void seize(Message message) {
        this.message = message;
        fixTime();
    }

    public short getReceiver() {
        return message.getReceiverSerialNumber();
    }

    public boolean messageNotYetDelivered() {
        return ! message.hasBeenDelivered();
    }

    public void markMessageAsDelivered() {
        message.markAsDelivered();
    }

    public short getSender() {
        return message.getSenderSerialNumber();
    }

    public double getSendTime() {
        return fixedTime;
    }

    public boolean messageHasBeenDelivered() {
        return message.hasBeenDelivered();
    }

    public void releaseToken() {
        message = null;
    }
}

