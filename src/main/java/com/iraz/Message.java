package com.iraz;

import lombok.Data;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

@Data
public class Message {
    private int cType;
    private int bUserId; //id of whom sent the message
    private byte[] message; //message itself
    private static final int INT_BYTES = Integer.BYTES*2;

    public Message(int cType, int bUserId, String message){
        this.cType=cType;
        this.bUserId=bUserId;
        this.message=message.getBytes(StandardCharsets.UTF_8);
    }

    public Message(int cType, int bUserId, byte[] message){
        this.cType=cType;
        this.bUserId=bUserId;
        this.message=message;
    }

    //converts message into byte[] array
    public byte[] toPacket(){
        return ByteBuffer.allocate(getAllBytes())
                .putInt(cType)
                .putInt(bUserId)
                .put(message).array();
    }

    //returns num of bytes of message string
    public int getMessageBytes(){
        return message.length;
    }

    //returns num of bytes of whole message
    public int getAllBytes(){
        return INT_BYTES +message.length;
    }

    public void encode(){
        try {
            message = Cryptor.getInstance().encrypt(new String(message));
        } catch (IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

    public void decode(){
        try {
            message =  Cryptor.getInstance().decrypt(message).getBytes(StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

}
