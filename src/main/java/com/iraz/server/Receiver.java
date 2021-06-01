package com.iraz.server;

import com.iraz.Packet;

import java.util.concurrent.BlockingQueue;

public class Receiver implements Runnable, ReceiverInterface{

    private final BlockingQueue<byte[]> input;
    private final BlockingQueue<byte[]> output;
    private static final int STOP=0;

    public Receiver(BlockingQueue<byte[]> input, BlockingQueue<byte[]> output){
        this.input=input;
        this.output=output;
    }

    @Override
    public void run() {
        receive();
    }

    private void receive(){
        try{
            while (true) {
                byte[] message=input.take();
                if(new Packet(message).getBMsg().getMessage().length==STOP){
                    System.err.println(Receiver.class+" got stop length message");
                    output.put(message);
                    break;
                }
                System.out.println("receiver "+Thread.currentThread().getId()+" got "+new String(new Packet(message).getBMsg().getMessage()));
                output.put(message);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void receiveMessage(byte[] message) {
        try {
            input.add(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BlockingQueue<byte[]> getOutput(){
        return output;
    }

}
