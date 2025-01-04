package net.lenni0451.miniconnect.model;

import io.netty.util.AttributeKey;

public class AttributeKeys {

    public static final AttributeKey<ConnectionInfo> CONNECTION_INFO = AttributeKey.newInstance("MiniConnect_ConnectionInfo");
    public static final AttributeKey<Boolean> ENABLE_HAPROXY = AttributeKey.newInstance("MiniConnect_EnableHAProxy");
    public static final AttributeKey<HandshakeData> HANDSHAKE_DATA = AttributeKey.newInstance("MiniConnect_HandshakeData");

}
