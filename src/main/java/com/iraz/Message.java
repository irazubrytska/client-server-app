package com.iraz;

import lombok.Data;

import javax.crypto.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;

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

    public void encode(Cryptor cryptor) throws NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        message = cryptor.encrypt(new String(message));
    }

    public void decode(Cryptor cryptor) throws NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        message =  cryptor.decrypt(message).getBytes(StandardCharsets.UTF_8);
    }

}
