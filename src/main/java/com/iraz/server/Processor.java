package com.iraz.server;

import com.iraz.Message;
import com.iraz.Packet;

import java.util.concurrent.BlockingQueue;

public class Processor implements Runnable{

    private final BlockingQueue<Packet> input;
    private final BlockingQueue<Packet> output;
    private static final int STOP=0;
    private final static String REPLY="Ok";

    public Processor(BlockingQueue<Packet> input, BlockingQueue<Packet> output){
        this.input=input;
        this.output=output;
    }

    @Override
    public void run() {
        process();
    }

    private void process(){
        try{
            while (true) {
                Packet pack=input.take();
                if(pack.getBMsg().getMessage().length==STOP){
                    output.put(pack);
                    System.err.println(Processor.class+" got stop length message");
                    break;
                }
                //output.put(pack);
                output.put(new Packet(pack.getBSrc(),pack.getBPktId(),new Message(pack.getBMsg().getCType(),pack.getBMsg().getBUserId(),REPLY)));
                System.out.println("processor "+Thread.currentThread().getId()+" got "+new String(pack.getBMsg().getMessage())+" "+REPLY);
            }
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }

}
