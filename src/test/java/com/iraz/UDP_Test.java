package com.iraz;
import com.google.common.primitives.UnsignedLong;
import com.iraz.store.*;
import org.junit.*;

import java.io.IOException;
import java.net.*;

public class UDP_Test {
    ClientUDP client;

    @Before
    public void setup() {
        try {
            new Thread(new ServerUDP()).start();
            client = new ClientUDP();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUdpClientServer() throws IOException {
        Message message = new Message(1,1,"some text");
        Packet packet = new Packet((byte)1, UnsignedLong.valueOf(1L),message);
        client.send(packet);
    }

    @After
    public void end() throws IOException {
        Message message = new Message(1,1,"end");
        Packet packet = new Packet((byte)1, UnsignedLong.valueOf(1L),message);
        client.send(packet);
        client.close();
    }

}
