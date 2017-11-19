package ru.ninefoldcomplex.tokenring.entities;

import ru.ninefoldcomplex.tokenring.utils.Utils;

/**
 * Created by ninefoldcomplex on 13.11.2017.
 */
public class Message {
    private double sendTime;
    private final short senderSerialNumber;
    private final short receiverSerialNumber;
    private byte[] messagePayload = new byte[3*1024]; //3 kBytes
    private boolean hasBeenDelivered;

    public Message(short senderSerialNumber, short receiverSerialNumber) {
        this.senderSerialNumber = senderSerialNumber;
        this.receiverSerialNumber = receiverSerialNumber;
    }

    public Message send() {
        this.sendTime = Utils.getTimeInSeconds();
        return this;
    }

    public double getSendTime() {
        return sendTime;
    }

    public short getSenderSerialNumber() {
        return senderSerialNumber;
    }

    public short getReceiverSerialNumber() {
        return receiverSerialNumber;
    }

    public boolean hasBeenDelivered() {
        return hasBeenDelivered;
    }

    public void markAsDelivered() {
        this.hasBeenDelivered = true;
    }

    public int getPayloadVolume() {
        return messagePayload.length;
    }
}
