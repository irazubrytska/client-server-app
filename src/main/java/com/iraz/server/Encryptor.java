package com.iraz.server;

import com.iraz.Message;
import com.iraz.Packet;

import java.security.*;
import java.util.concurrent.BlockingQueue;
import com.google.common.primitives.UnsignedLong;

import javax.crypto.*;

public class Encryptor implements Runnable{

    private final BlockingQueue<Message> input;
    private final BlockingQueue<Packet> output;
    private static final int STOP=0;

    public Encryptor(BlockingQueue<Message> input, BlockingQueue<Packet> output){
        this.input=input;
        this.output=output;
    }

    @Override
    public void run() {
        encrypt();
    }

    private void encrypt(){
        try{
            while (true) {
                Message message=input.take();
                if(message.getMessage().length==STOP){
                    output.put(new Packet((byte)0,UnsignedLong.ZERO, message));
                    System.err.println(Encryptor.class+" got stop length message");
                    break;
                }
                message.encode();
                output.put(new Packet((byte)1, UnsignedLong.ONE, message));
            }
        }
        catch(InterruptedException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException
                | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e){
            e.printStackTrace();
        }
    }

}
