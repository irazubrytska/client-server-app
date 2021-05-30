package com.iraz.server;

import com.iraz.Packet;

import javax.crypto.*;
import java.net.InetAddress;
import java.security.*;
import java.util.concurrent.BlockingQueue;

public class Sender implements Runnable{

    private final BlockingQueue<Packet> input;
    private final InetAddress address;
    private static final int STOP=0;

    public Sender(BlockingQueue<Packet> input, InetAddress address){
        this.input = input;
        this.address = address;
    }

    @Override
    public void run() {
        send();
    }

    private void send(){
        try {
            Packet pack=input.take();
            if(pack.getBMsg().getMessage().length==STOP){
                return;
            }
            sendMessage(pack.toPacket());
        } catch(InterruptedException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException
                | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e){
            e.printStackTrace();
        }
    }

    private void sendMessage(byte[] message){
        System.out.println("Sent message");
    }

}
