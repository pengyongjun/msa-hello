package com.amwalle.msahello.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @program: msa-hello
 * @description: 用于获取当前服务的地址信息
 * @author: pengyongjun
 * @create: 2021.06.27 11:21
 **/
public class Address {
    public static String getIpAddress() {
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
        return ip.getHostAddress();
    }
}
