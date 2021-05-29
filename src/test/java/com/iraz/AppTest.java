package com.iraz;

import  com.google.common.primitives.UnsignedLong;
import org.checkerframework.checker.units.qual.C;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.*;
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
    public void encryptDecryptTest() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
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

}
