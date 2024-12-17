package net.lenni0451.miniconnect.proxy;

import com.google.common.cache.CacheBuilder;
import net.lenni0451.miniconnect.model.ConnectionInfo;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StateRegistry {

    private final Map<InetAddress, ConnectionInfo> connectionTargets = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).<InetAddress, ConnectionInfo>build().asMap();
    private final Map<InetAddress, ConnectionInfo> reconnectTargets = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).<InetAddress, ConnectionInfo>build().asMap();

    public Map<InetAddress, ConnectionInfo> getConnectionTargets() {
        return this.connectionTargets;
    }

    public Map<InetAddress, ConnectionInfo> getReconnectTargets() {
        return this.reconnectTargets;
    }

}
