package com.iraz.store;

import com.iraz.Packet;

import java.io.IOException;
import java.net.SocketException;

public interface TransportProtocol {

    Packet receive() throws IOException;
    void send(Packet packet) throws IOException;
    void close() throws IOException;

}
