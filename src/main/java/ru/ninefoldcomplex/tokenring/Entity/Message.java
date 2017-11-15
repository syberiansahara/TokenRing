package ru.ninefoldcomplex.tokenring.Entity;

/**
 * Created by ninefoldcomplex on 13.11.2017.
 */
public class Message {
    private final short senderSerialNumber;
    private final short receiverSerialNumber;
    private byte[] messagePayload = new byte[3*1024]; //3 kBytes

    public Message(short senderSerialNumber, short receiverSerialNumber) {
        this.senderSerialNumber = senderSerialNumber;
        this.receiverSerialNumber = receiverSerialNumber;
    }

    public short getSenderSerialNumber() {
        return senderSerialNumber;
    }

    public short getReceiverSerialNumber() {
        return receiverSerialNumber;
    }
}
