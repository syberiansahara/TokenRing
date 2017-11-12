package ru.ninefoldcomplex.tokenring;

public class Frame {
    private final short serialNumber;
    private byte token;
    private short sender;
    private short receiver;
    private byte[] message = new byte[0];

    public Frame(short serialNumber) {
        this.serialNumber = serialNumber;
    }

    public short getSerialNumber() {
        return serialNumber;
    }

    public short getSender() {
        return sender;
    }

    public short getReceiver() {
        return receiver;
    }

    public boolean frameIsEmpty() {
        return (token == 0 && message.length == 0);
    }

    public void seizeFrame(short sender, short receiver) {
        token = 1;
        this.sender = sender;
        this.receiver = receiver;
        message = new byte[3*1024]; //3 kBytes message
    }

    public boolean messageNotYetDelivered() {
        return (token == 1);
    }

    public void releaseToken() {
        token = 0;
    }

    public boolean messageDelivered() {
        return (token == 0 && message.length != 0);
    }


    public void clearMessage() {
        if (token == 0) message = new byte[0];
        else throw new RuntimeException("Impossible to clear message while the token is not set to 0");
    }
}

