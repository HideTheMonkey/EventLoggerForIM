package com.hidethemonkey.elfim.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;

// This class contains utility methods for network operations
public class NetworkUtils {
    /**
     * Get the external IP address of the machine
     * @return
     */
    public static String getExternalIP() {
        try {
            URL ipchecker = URI.create("http://checkip.amazonaws.com").toURL();
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(ipchecker.openStream()));
                return in.readLine();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // do nothing
                    }
                }
            }

        } catch (IOException e) {
            // do nothing
            return null;
        }
    }

    /**
     * Gets the local IP address of the machine, or returns the server.properties IP address if provided
     * @param ip - the IP address in server.properties
     * @return
     */
    public static String getLocalIP(String ip) {
        if (ip != null && !ip.isEmpty()) {
            return ip;
        }
        Socket socket = null;
        String localIP = "";
        try {
            socket = new Socket();
            // this doesn't actually connect to google, it just uses it to find the right interface
            socket.connect(new InetSocketAddress("google.com", 80));
            localIP = socket.getLocalAddress().getHostAddress();
        } catch (IOException e) {
            // do nothing
        }
        finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                // do nothing
            }
        }
        return localIP;
    }
}
