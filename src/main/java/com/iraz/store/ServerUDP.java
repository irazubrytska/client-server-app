package com.iraz.store;

import com.iraz.Packet;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class ServerUDP implements TransportProtocol, Runnable{

    private InetAddress host;
    private int port;
    private final DatagramSocket socket;

    public ServerUDP() throws SocketException {
        this.host=Constants.LOCALHOST;
        this.port=Constants.DEFAULT_PORT;
        socket=new DatagramSocket(port); //connection
    }

    @Override
    public void run() {
        boolean running = true;
        while(running){
            Packet packet = receive();
            if((new String(packet.getBMsg().getMessage()).equals("end"))){
                running =false;
                continue;
            }
            try {
                send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //socket.close();
    }

    @Override
    public Packet receive(){
        try {
            byte[] maxPacketBuffer = new byte[Packet.MAX_PACKET_SIZE];
            DatagramPacket datagramPacket = new DatagramPacket(maxPacketBuffer,maxPacketBuffer.length,host,port);
            socket.receive(datagramPacket);
            System.out.println(ServerUDP.class+" received "+ Arrays.toString(maxPacketBuffer));

            host=datagramPacket.getAddress();
            port=datagramPacket.getPort();

            Packet packetReceived=new Packet(maxPacketBuffer);
            System.out.println("received message: "+new String(packetReceived.getBMsg().getMessage()));

            return packetReceived;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void send(Packet packet) throws IOException {
        byte[] packetBytes=packet.toPacket();
        DatagramPacket datagramPacket=new DatagramPacket(packetBytes,packetBytes.length,host,port);
        socket.send(datagramPacket);
        System.out.println(ServerUDP.class+" send "+Arrays.toString(packetBytes));
    }

    @Override
    public void close() {
        socket.close();
    }

}
