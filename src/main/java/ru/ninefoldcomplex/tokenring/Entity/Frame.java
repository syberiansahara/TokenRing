package ru.ninefoldcomplex.tokenring.Entity;

public class Frame {
    private final short frameSerialNumber;
    private byte token;
    private short sender;
    private short receiver;
    private byte[] framePayload = new byte[0];

    public Frame(short frameSerialNumber) {
        this.frameSerialNumber = frameSerialNumber;
    }

    public short getFrameSerialNumber() {
        return frameSerialNumber;
    }

    public short getSender() {
        return sender;
    }

    public short getReceiver() {
        return receiver;
    }

    public boolean isEmpty() {
        return (token == 0 && framePayload.length == 0);
    }

    public void seize(short sender, Message message) {
        token = 1;
        this.sender = sender;
        this.receiver = message.getReceiverSerialNumber();
        framePayload = new byte[3*1024]; //3 kBytes framePayload
    }

    public boolean messageNotYetDelivered() {
        return (token == 1);
    }

    public void releaseToken() {
        token = 0;
    }

    public boolean messageDelivered() {
        return (token == 0 && framePayload.length != 0);
    }


    public void clearMessage() {
        if (token == 0) framePayload = new byte[0];
        else throw new RuntimeException("Impossible to clear framePayload while the token is not set to 0");
    }
}

