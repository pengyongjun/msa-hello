package com.amwalle.mschello.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.*;
import java.util.Enumeration;

@RestController
public class HelloMsa {
    @RequestMapping(method = RequestMethod.GET, path = "/hello-msa")
    public String helloMsa() {
        Enumeration<NetworkInterface> allNetInterfaces;
        InetAddress ip = null;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = addresses.nextElement();
                    if (ip instanceof Inet4Address) {
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            return "Get host information error! " + e.getMessage();
        }

        assert ip != null;
        String hostAddress = ip.getHostAddress();
        return "Hello, this service is from " + hostAddress + ".";
    }
}
