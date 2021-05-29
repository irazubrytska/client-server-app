package com.iraz.server;

import com.iraz.Message;

import java.util.concurrent.BlockingQueue;

public class Receiver implements Runnable{

    private BlockingQueue<Message> input;
    private BlockingQueue<Message> output;
    private int stop;

    public Receiver(BlockingQueue<Message> input, BlockingQueue<Message> output, int stop){
        this.input=input;
        this.output=output;
        this.stop=stop;
    }

    @Override
    public void run() {
        receiveMessage();
    }

    private void receiveMessage(){
        try{
            while (true) {
                Message message=input.take();
                output.put(message);
                if(message.getMessage().length==stop){
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
