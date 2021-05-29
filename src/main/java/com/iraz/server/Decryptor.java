package com.iraz.server;

import com.iraz.Message;

import java.security.*;
import java.util.concurrent.BlockingQueue;

import javax.crypto.*;

public class Decryptor implements Runnable{

    private final BlockingQueue<Message> input;
    private final BlockingQueue<Message> output;
    private final int stop;

    public Decryptor(BlockingQueue<Message> input, BlockingQueue<Message> output, int stop){
        this.input=input;
        this.output=output;
        this.stop=stop;
    }

    @Override
    public void run() {
        decrypt();
    }

    private void decrypt(){
        try{
            while (true) {
                Message message=input.take();
                if(message.getMessage().length==stop){
                    output.put(message);
                    System.err.println(Decryptor.class+" got stop length message");
                    break;
                }
                message.decode();
                output.put(message);
            }
        }
        catch(InterruptedException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException
                | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e){
            e.printStackTrace();
        }
    }

    public BlockingQueue<Message> getOutput(){
        return output;
    }

}
