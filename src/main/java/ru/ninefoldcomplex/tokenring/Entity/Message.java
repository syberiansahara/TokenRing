package ru.ninefoldcomplex.tokenring.Entity;

public class Message {
    private final short receiverSerialNumber;

    public Message(short receiverSerialNumber) {
        this.receiverSerialNumber = receiverSerialNumber;
    }

    public short getReceiverSerialNumber() {
        return receiverSerialNumber;
    }
}
