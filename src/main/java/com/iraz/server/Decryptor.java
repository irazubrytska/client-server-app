package com.iraz.server;

import com.iraz.Packet;

import java.util.concurrent.BlockingQueue;

public class Decryptor implements Runnable{

    private final BlockingQueue<byte[]> input;
    private final BlockingQueue<Packet> output;
    private static final int STOP=0;

    public Decryptor(BlockingQueue<byte[]> input, BlockingQueue<Packet> output){
        this.input=input;
        this.output=output;
    }

    @Override
    public void run() {
        decrypt();
    }

    private void decrypt(){
        try{
            while (true) {
                byte[] message=input.take();
                if(new Packet(message).getBMsg().getMessage().length==STOP){
                    output.put(new Packet(message));
                    System.err.println(Decryptor.class+" got stop length message");
                    break;
                }
                output.put(new Packet(message)); //decoding
                System.out.println("decryptor "+Thread.currentThread().getId()+" got "+new String(new Packet(message).getBMsg().getMessage()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BlockingQueue<Packet> getOutput(){
        return output;
    }

}
