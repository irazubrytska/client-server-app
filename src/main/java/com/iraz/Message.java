package com.iraz;

import lombok.Data;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Data
public class Message {
    private int cType;
    private int bUserId; //id of whom sent the message
    private byte[] message; //message itself

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
        return Integer.BYTES*2+message.length;
    }

    public void encode() throws NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        message = Cryptor.getInstance().encrypt(new String(message));
    }

    public void decode() throws NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        message =  Cryptor.getInstance().decrypt(message).getBytes(StandardCharsets.UTF_8);
    }

}
