package com.iraz.server;

import com.iraz.Packet;

import java.util.concurrent.BlockingQueue;

public class Encryptor implements Runnable{

    private final BlockingQueue<Packet> input;
    private final BlockingQueue<byte[]> output;
    private static final int STOP=0;

    public Encryptor(BlockingQueue<Packet> input, BlockingQueue<byte[]> output){
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
                Packet message=input.take();
                if(message.getBMsg().getMessage().length==STOP){
                    output.put(message.toPacket());
                    System.err.println(Encryptor.class+" got stop length message");
                    break;
                }
                System.out.println("encryptor "+Thread.currentThread().getId()+" got "+new String(message.getBMsg().getMessage()));
                output.put(message.toPacket()); //encoding
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
