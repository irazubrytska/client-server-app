package com.iraz;

import  com.google.common.primitives.UnsignedLong;
import com.iraz.server.*;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.*;
import java.net.InetAddress;
import java.security.*;

public class AppTest {

    @Test
    public void createPacket(){
        byte b=0;
        UnsignedLong packetId=UnsignedLong.valueOf(10L);
        String str="this is some message";
        int cType=10;
        int userId=2;
        Message message=new Message(cType,userId,str);
        Packet pack=new Packet(b,packetId,message);
    }

    @Test
    public void encryptDecryptTest() throws NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException{
        String original = "This is the message I want To Encrypt.";
        byte[] encrypted = Cryptor.getInstance().encrypt(original);
        String decrypted = Cryptor.getInstance().decrypt(encrypted);
        Assert.assertEquals(original,decrypted);
//        System.out.println("original: "+original);
//        System.out.println("encrypted: "+ DatatypeConverter.printHexBinary(encrypted));
//        System.out.println("decrypted: "+decrypted);
    }

    @Test
    public void packetTest() throws Exception {
        //same packages
        String test_message="this is some test message";
        Packet pack1 = new Packet((byte) 6, UnsignedLong.valueOf(1L), new Message(5, 4, test_message));
        Packet pack2 = new Packet((byte) 6, UnsignedLong.valueOf(1L), new Message(5, 4, test_message));

        //encode both
        byte[] pack1Bytes=pack1.toPacket();
        byte[] pack2Bytes=pack2.toPacket();

        //decode both
        pack1=new Packet(pack1Bytes);
        pack2=new Packet(pack2Bytes);

        Assert.assertEquals(pack1.getBSrc(), pack2.getBSrc());
        Assert.assertEquals(pack1.getBPktId(), pack2.getBPktId());
        Assert.assertEquals(pack1.getWLen(), pack2.getWLen());

        Assert.assertEquals(pack1.getWCrc16_1(), pack2.getWCrc16_1());
        Assert.assertEquals(pack1.getWCrc16_2(), pack2.getWCrc16_2());

        Assert.assertEquals(pack1.getBMsg().getCType(), pack2.getBMsg().getCType());
        Assert.assertEquals(pack1.getBMsg().getBUserId(), pack2.getBMsg().getBUserId());
        Assert.assertArrayEquals(pack1.getBMsg().getMessage(), pack2.getBMsg().getMessage());
        Assert.assertEquals(test_message, new String(pack2.getBMsg().getMessage()));
    }

    @Test
    public void serverTest() throws Exception {
        Server server=new Server(8,20);
        String message="some message";
        String reply="Ok";
        server.createEqualMessages(message);
        int nMessages=server.getInitialMessages().size();
        Thread[] threads = new Thread[server.getNUM_THREADS()];

        //______________________________receivers_____________________
        for(int i=0; i<threads.length; i++){
            threads[i]=new Thread(new Receiver(server.getInitialMessages(), server.getReceiverOutput()));
            threads[i].start();
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Assert.assertEquals(nMessages,server.getReceiverOutput().size()); //input.size==output.size

        //______________________________decryptors_____________________
        for(int i=0; i<threads.length; i++){
            threads[i]=new Thread(new Decryptor(server.getReceiverOutput(), server.getDecryptorOutput()));
            threads[i].start();
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Assert.assertEquals(nMessages,server.getDecryptorOutput().size()); //input.size==output.size
        for (Packet p : server.getDecryptorOutput()) {
            if (!new String(p.getBMsg().getMessage()).equals("")) //if not stop message
                Assert.assertEquals(new String(p.getBMsg().getMessage()), message); //check if decryption is working
        }

        //______________________________processors_____________________
        for(int i=0; i<threads.length; i++){
            threads[i]=new Thread(new Processor(server.getDecryptorOutput(), server.getProcessorOutput()));
            threads[i].start();
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Assert.assertEquals(nMessages,server.getProcessorOutput().size()); //input.size==output.size
        for (Packet p : server.getProcessorOutput()) {
            if (!new String(p.getBMsg().getMessage()).equals("")) {//if not stop message
                Assert.assertEquals(new String(p.getBMsg().getMessage()), reply); //check if processor is working
            }
        }

        //______________________________encryptors_____________________
        for(int i=0; i<threads.length; i++){
            threads[i]=new Thread(new Encryptor(server.getProcessorOutput(), server.getEncryptorOutput()));
            threads[i].start();
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Assert.assertEquals(nMessages,server.getEncryptorOutput().size()); //input.size=output.size
//        System.out.println(Arrays.toString(message.getBytes(StandardCharsets.UTF_8)));
//        System.out.println(Arrays.toString(new Packet(server.getEncryptorOutput().take()).getBMsg().getMessage()));
        for (byte[] b : server.getEncryptorOutput()) {
            if (!new String(new Packet(b).getBMsg().getMessage()).equals("")) {//if not stop message
                Assert.assertEquals(new String(new Packet(b).getBMsg().getMessage()), reply); //check if encryption is working
            }
        }

        //______________________________senders_____________________
        for(int i=0; i<threads.length; i++){
            threads[i]=new Thread(new Sender(server.getEncryptorOutput(), InetAddress.getLocalHost()));
            threads[i].start();
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
