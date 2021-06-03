package com.iraz.store;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Constants {

    public static final String UDP="udp";
    public static final String TCP="tcp";

    public static InetAddress LOCALHOST = null;

    static {
        try {
            LOCALHOST = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static final int DEFAULT_PORT=2030;

}
