package ru.ninefoldcomplex.tokenring.Entity;

public class Frame {
    private final short frameSerialNumber;
    private byte token;
    private Message message;
    private long fixedTime;

    public Frame(short frameSerialNumber) {
        this.frameSerialNumber = frameSerialNumber;
        fixedTime = System.nanoTime();
    }

    public short getFrameSerialNumber() {
        return frameSerialNumber;
    }

    public boolean isEmpty() {
        return (token == 0 && message == null);
    }

    public short getSender() {
        return message.getSenderSerialNumber();
    }

    public short getReceiver() {
        return message.getReceiverSerialNumber();
    }

    public long getSendTime() {
        return fixedTime;
    }

    public void seize(short sender, Message message) {
        token = 1;
        this.message = message;
        fixedTime = System.nanoTime();
    }

    public boolean messageNotYetDelivered() {
        return (token == 1);
    }

    public void releaseToken() {
        token = 0;
    }

    public boolean messageDelivered() {
        return (token == 0 && message != null);
    }

    public void clearMessage() {
        if (token == 0) message = null;
        else throw new RuntimeException("Impossible to clear framePayload while the token is not set to 0");
    }
}

