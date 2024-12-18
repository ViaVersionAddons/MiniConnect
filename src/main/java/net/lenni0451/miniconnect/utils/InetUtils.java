package net.lenni0451.miniconnect.utils;

import java.net.InetAddress;

public class InetUtils {

    public static boolean isLocal(final InetAddress address) {
        if (address.isAnyLocalAddress() || address.isLoopbackAddress()) return true;
        byte[] addressBytes = address.getAddress();

        if (addressBytes.length == 4) { // Check for IPv4 local address ranges
            if (addressBytes[0] == 10) return true; // 10.0.0.0/8
            if (addressBytes[0] == (byte) 172 && addressBytes[1] >= 16 && addressBytes[1] <= 31) return true; // 172.16.0.0/12
            if (addressBytes[0] == (byte) 192 && addressBytes[1] == (byte) 168) return true; // 192.168.0.0/16
        }
        if (addressBytes.length == 16) { // Check for IPv6 local address ranges
            return (addressBytes[0] == (byte) 0xfe && (addressBytes[1] & (byte) 0xc0) == (byte) 0x80); // fe80::/10
        }
        return false;
    }

}
