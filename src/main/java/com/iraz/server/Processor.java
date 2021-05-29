package com.iraz.server;

import com.iraz.Message;

import java.util.concurrent.BlockingQueue;

public class Processor implements Runnable{

    private final BlockingQueue<Message> input;
    private final BlockingQueue<Message> output;
    private final int stop;
    private final static String REPLY="Ok";

    public Processor(BlockingQueue<Message> input, BlockingQueue<Message> output, int stop){
        this.input=input;
        this.output=output;
        this.stop=stop;
    }

    @Override
    public void run() {
        process();
    }

    private void process(){
        try{
            while (true) {
                Message message=input.take();
                if(message.getMessage().length==stop){
                    output.put(message);
                    System.err.println(Processor.class+" got stop length message");
                    break;
                }
                Message reply=new Message(message.getCType(),message.getBUserId(),REPLY);
                output.put(reply);
            }
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }

}
