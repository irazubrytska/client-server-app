package com.iraz.server;

import com.iraz.Packet;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.net.InetAddress;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;

public class Sender implements Runnable{

    private BlockingQueue<Packet> input;
    private InetAddress address;
    private int stop;

    public Sender(BlockingQueue<Packet> input, InetAddress address, int stop){
        this.input = input;
        this.address = address;
        this.stop=stop;
    }

    @Override
    public void run() {
        send();
    }

    private void send(){
        try {
            Packet pack=input.take();
            if(pack.getBMsg().getMessage().length==stop){
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
