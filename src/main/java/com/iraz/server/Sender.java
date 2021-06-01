package com.iraz.server;

import com.iraz.Packet;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

public class Sender implements Runnable{

    private final BlockingQueue<byte[]> input;
    private final InetAddress address;
    private static final int STOP=0;

    public Sender(BlockingQueue<byte[]> input, InetAddress address){
        this.input = input;
        this.address = address;
    }

    @Override
    public void run() {
        send();
    }

    private void send(){
            while(true){
                byte[] pack;
                try {
                    pack = input.take();
                if((new Packet(pack)).getBMsg().getMessage().length==STOP){
                    System.err.println(Sender.class+" got stop length message");
                    break;
                }
                sendMessage(pack);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    private void sendMessage(byte[] message) throws Exception {
        System.out.println("sender sent message "+new String(new Packet(message).getBMsg().getMessage()));
    }

}
