package net.lenni0451.miniconnect.proxy;

import com.google.common.cache.CacheBuilder;
import net.lenni0451.miniconnect.model.ConnectionInfo;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class StateRegistry {

    private final Map<InetAddress, ConnectionInfo> connectionTargets = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).<InetAddress, ConnectionInfo>build().asMap();
    private final Map<InetAddress, ConnectionInfo> reconnectTargets = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).<InetAddress, ConnectionInfo>build().asMap();
    private final Map<InetAddress, ConnectionInfo> lobbyTargets = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).<InetAddress, ConnectionInfo>build().asMap();
    private final Set<UUID> verificationQueue = Collections.newSetFromMap(CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).<UUID, Boolean>build().asMap());

    public Map<InetAddress, ConnectionInfo> getConnectionTargets() {
        return this.connectionTargets;
    }

    public Map<InetAddress, ConnectionInfo> getReconnectTargets() {
        return this.reconnectTargets;
    }

    public Map<InetAddress, ConnectionInfo> getLobbyTargets() {
        return this.lobbyTargets;
    }

    public Set<UUID> getVerificationQueue() {
        return this.verificationQueue;
    }

}
