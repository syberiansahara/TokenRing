package ru.ninefoldcomplex.tokenring.entities;

/**
 * Created by ninefoldcomplex on 12.11.2017.
 */
public class Frame {
    private final short frameSerialNumber;
    private Message message;

    public Frame(short frameSerialNumber) {
        this.frameSerialNumber = frameSerialNumber;
    }

    public short getFrameSerialNumber() {
        return frameSerialNumber;
    }

    public boolean isToken() {
        return message == null;
    }

    public void sendMessage(Message message) {
        this.message = message.send();
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
        return message.getSendTime();
    }

    public int getPayloadVolume() {
        return message.getPayloadVolume();
    }

    public boolean messageHasBeenDelivered() {
        return message.hasBeenDelivered();
    }

    public void releaseToken() {
        message = null;
    }

}

