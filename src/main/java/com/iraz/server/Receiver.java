package com.iraz.server;

import com.iraz.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Receiver implements Runnable{

    private final BlockingQueue<Message> input;
    private final BlockingQueue<Message> output;
    private static final int STOP=0;

    public Receiver(BlockingQueue<Message> input, BlockingQueue<Message> output){
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
                Message message=input.take();
                output.put(message);
                if(message.getMessage().length==STOP){
                    System.err.println(Receiver.class+" got stop length message");
                    break;
                }
            }
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    public BlockingQueue<Message> getOutput(){
        return output;
    }
}
