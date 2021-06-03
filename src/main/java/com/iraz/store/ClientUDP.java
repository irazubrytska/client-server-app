package com.iraz.store;

import com.iraz.Packet;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class ClientUDP implements TransportProtocol{

    private InetAddress host;
    private int port;
    private final DatagramSocket socket;

    public ClientUDP() throws SocketException {
        this.host=Constants.LOCALHOST;
        this.port=Constants.DEFAULT_PORT;
        socket=new DatagramSocket(); //connection
    }

    public ClientUDP(InetAddress host, int port) throws SocketException {
        this.host=host;
        if(host==null)
            this.host=Constants.LOCALHOST;
        this.port=port;
        if(port<1023) //reserved ports
            this.port=Constants.DEFAULT_PORT;
        socket=new DatagramSocket(port);
    }

    @Override
    public void send(Packet packet) throws IOException {
        byte[] packetBytes=packet.toPacket();
        DatagramPacket datagramPacket=new DatagramPacket(packetBytes,packetBytes.length,host,port);
        socket.send(datagramPacket);
        System.out.println(ClientUDP.class+" send "+Arrays.toString(packetBytes));
    }

    @Override
    public Packet receive(){
        try {
            byte[] maxPacketBuffer = new byte[Packet.MAX_PACKET_SIZE];
            DatagramPacket datagramPacket = new DatagramPacket(maxPacketBuffer,maxPacketBuffer.length,host,port);
            socket.receive(datagramPacket);
            System.out.println(ClientUDP.class+" received "+ Arrays.toString(maxPacketBuffer));

            Packet packetReceived=new Packet(maxPacketBuffer);
            System.out.println("received message: "+new String(packetReceived.getBMsg().getMessage()));

            packetReceived.setAddress(datagramPacket.getAddress());
            packetReceived.setPort(datagramPacket.getPort());

            return packetReceived;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {
        socket.close();
    }

}
