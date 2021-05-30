package com.iraz.server;

import com.iraz.Message;
import com.iraz.Packet;
import org.apache.commons.lang3.RandomStringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {

    private static final int NUM_THREADS=8;
    private static final int NUM_MESSAGES=100;
    private final BlockingQueue<Message> initialMessages;
    private final BlockingQueue<Message> receiverOutput;
    private final BlockingQueue<Message> decryptorOutput;
    private final BlockingQueue<Message> processorOutput;
    private final BlockingQueue<Packet> encryptorOutput;

    public Server(){
        initialMessages=new LinkedBlockingQueue<>();
        receiverOutput=new LinkedBlockingQueue<>();
        decryptorOutput =new LinkedBlockingQueue<>();
        processorOutput=new LinkedBlockingQueue<>();
        encryptorOutput=new LinkedBlockingQueue<>();
    }

    public void simulate(){
        if(initialMessages.isEmpty()){
            createMessages();
        }
        launchReceivers();
        launchDecryptors();
        launchProcessors();
        launchEncryptors();
        launchSenders();
    }

    private void createMessages(){
        for(int i=0; i<NUM_MESSAGES; i++){
            try {
                initialMessages.put(new Message(1,1,RandomStringUtils.randomAlphabetic(10)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void launchReceivers(){
        for(int i=0; i<NUM_THREADS; i++){
            new Thread(new Receiver(initialMessages,receiverOutput));
        }
    }

    private void launchDecryptors(){
        for(int i=0; i<NUM_THREADS; i++){
            new Thread(new Decryptor(receiverOutput,decryptorOutput));
        }
    }

    private void launchProcessors(){
        for(int i=0; i<NUM_THREADS; i++){
            new Thread(new Processor(decryptorOutput,processorOutput));
        }
    }

    private void launchEncryptors(){
        for(int i=0; i<NUM_THREADS; i++){
            new Thread(new Encryptor(processorOutput,encryptorOutput));
        }
    }

    private void launchSenders(){
        for(int i=0; i<NUM_THREADS; i++){
            try {
                new Thread(new Sender(encryptorOutput,InetAddress.getByName("https://www.google.com.ua/")));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

}


