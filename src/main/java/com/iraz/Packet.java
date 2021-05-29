package com.iraz;

import com.github.snksoft.crc.CRC;
import com.google.common.primitives.UnsignedLong;
import lombok.Data;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Data
public class Packet {
    public final static Byte bMagic=0x13; //byte that points to start of packet
    private Byte bSrc;                   //unique customer application number
    private UnsignedLong bPktId;        //packet id
    private Integer wLen;              //length of packet
    private Short wCrc16_1;           //00-13
    private Message bMsg;            //message
    private Short wCrc16_2;         //16-16+wLen

    //creates new packet
    public Packet(Byte bSrc, UnsignedLong bPktId, Message bMsg){
        this.bSrc=bSrc;
        this.bPktId=bPktId;
        this.bMsg=bMsg;
        wLen=bMsg.getAllBytes();
    }

    //decodes given packet presented in byte[] array
    public Packet(byte[] encoded) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(encoded);
        //first part
        Byte expectedMagicByte=buffer.get();
        if(!expectedMagicByte.equals(bMagic)){
            throw new Exception("Unexpected magic byte");
        }
        bSrc=buffer.get();
        bPktId=UnsignedLong.fromLongBits(buffer.getLong());
        wLen=buffer.getInt();
        int firstPartLength=bMagic.BYTES+bSrc.BYTES+Long.BYTES+wLen.BYTES;
        byte[] firstPart=ByteBuffer.allocate(firstPartLength)
                .put(bMagic)
                .put(bSrc)
                .putLong(bPktId.longValue())
                .putInt(wLen)
                .array();
        Short wCrc16_1Calculated = (short) CRC.calculateCRC(CRC.Parameters.CRC16, firstPart);
        wCrc16_1=buffer.getShort();
        if(!wCrc16_1Calculated.equals(wCrc16_1)){
            throw new Exception("Unexpected crc16_1");
        }
        //second part
        int cType=buffer.getInt();
        int userId=buffer.getInt();
        byte[] messageByteArr=new byte[wLen];
        buffer.get(messageByteArr);
        bMsg=new Message(cType,userId,messageByteArr);
        byte[] secondPart = ByteBuffer.allocate(bMsg.getAllBytes())
                .put(bMsg.toPacket())
                .array();
        Short wCrc16_2Calculated=(short) CRC.calculateCRC(CRC.Parameters.CRC16,secondPart);
        wCrc16_2=buffer.getShort();
        if (!wCrc16_2Calculated.equals(wCrc16_2)) {
            throw new Exception("Unexpected crc16_2");
        }
        bMsg.decode();
    }

    //encodes packet
    public byte[] toPacket() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        Message message=getBMsg();
        message.encode();
        wLen=message.getMessage().length;

        int firstPartLength=bMagic.BYTES+bSrc.BYTES+Long.BYTES+wLen.BYTES;
        byte[] firstPart = ByteBuffer.allocate(firstPartLength)
                .put(bMagic)
                .put(bSrc)
                .putLong(bPktId.longValue())
                .putInt(wLen)
                .array();
        wCrc16_1=(short) CRC.calculateCRC(CRC.Parameters.CRC16,firstPart);

        int secondPartLength=message.getAllBytes();
        byte[] secondPart = ByteBuffer.allocate(secondPartLength)
                .put(message.toPacket())
                .array();
        wCrc16_2=(short) CRC.calculateCRC(CRC.Parameters.CRC16,secondPart);

        int totalLength=firstPartLength+wCrc16_1.BYTES+secondPartLength+wCrc16_2.BYTES;

        return ByteBuffer.allocate(totalLength)
                .put(firstPart).putShort(wCrc16_1)
                .put(secondPart).putShort(wCrc16_2).array();
    }

}
