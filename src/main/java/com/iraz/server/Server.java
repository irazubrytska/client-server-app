package com.iraz.server;

import com.google.common.primitives.UnsignedLong;
import com.iraz.Message;
import com.iraz.Packet;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.*;

@Data
public class Server {

    private int NUM_THREADS;
    private int NUM_MESSAGES;
    private final BlockingQueue<byte[]> initialMessages;
    private final BlockingQueue<byte[]> receiverOutput;
    private final BlockingQueue<Packet> decryptorOutput;
    private final BlockingQueue<Packet> processorOutput;
    private final BlockingQueue<byte[]> encryptorOutput;

    public Server(int threads, int messages){
        this.NUM_THREADS=threads;
        this.NUM_MESSAGES=messages;
        initialMessages=new LinkedBlockingQueue<>();
        receiverOutput=new LinkedBlockingQueue<>();
        decryptorOutput =new LinkedBlockingQueue<>();
        processorOutput=new LinkedBlockingQueue<>();
        encryptorOutput=new LinkedBlockingQueue<>();
    }

    public void work(){
        if(initialMessages.isEmpty()){
            createRandomMessages();
        }
        launchReceivers();
        launchDecryptors();
        launchProcessors();
        launchEncryptors();
        launchSenders();
    }

    public void createRandomMessages(){
        for(int i=0; i<NUM_MESSAGES; i++){  //random packages
            Packet p = new Packet((byte) i, UnsignedLong.valueOf(i),new Message(1, i, RandomStringUtils.randomAlphabetic(10)));
            initialMessages.add(p.toPacket());

        }
        for(int i=0; i<NUM_THREADS; i++){  //stop messages(empty)
            initialMessages.add(new Packet((byte) i, UnsignedLong.valueOf(i),new Message(1, i, "")).toPacket());
        }
    }

    public void createEqualMessages(String message){
        for(int i=0; i<NUM_MESSAGES; i++){
            Packet p = new Packet((byte) i, UnsignedLong.valueOf(i),new Message(1, i, message));
            initialMessages.add(p.toPacket());

        }
        for(int i=0; i<NUM_THREADS; i++){  //stop messages(empty)
            initialMessages.add(new Packet((byte) i, UnsignedLong.valueOf(i),new Message(1, i, "")).toPacket());
        }
    }

    private void launchReceivers(){
        for(int i=0; i<NUM_THREADS; i++){
            new Thread(new Receiver(initialMessages,receiverOutput)).start();
        }
    }

    private void launchDecryptors(){
        for(int i=0; i<NUM_THREADS; i++){
            new Thread(new Decryptor(receiverOutput,decryptorOutput)).start();
        }
    }

    private void launchProcessors(){
        for(int i=0; i<NUM_THREADS; i++){
            new Thread(new Processor(decryptorOutput,processorOutput)).start();
        }
    }

    private void launchEncryptors(){
        for(int i=0; i<NUM_THREADS; i++){
            new Thread(new Encryptor(processorOutput,encryptorOutput)).start();
        }
    }

    private void launchSenders(){
        for(int i=0; i<NUM_THREADS; i++){
            try {
                new Thread(new Sender(encryptorOutput, InetAddress.getLocalHost())).start();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

}


