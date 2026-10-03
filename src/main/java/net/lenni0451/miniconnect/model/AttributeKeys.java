package net.lenni0451.miniconnect.model;

import io.netty.util.AttributeKey;

public class AttributeKeys {

    public static final String SKIP_BACKEND_PROXY_NAME = "MiniConnect_SkipBackendProxy";

    public static final AttributeKey<ConnectionInfo> CONNECTION_INFO = AttributeKey.newInstance("MiniConnect_ConnectionInfo");
    public static final AttributeKey<Boolean> ENABLE_HAPROXY = AttributeKey.newInstance("MiniConnect_EnableHAProxy");
    public static final AttributeKey<HandshakeData> HANDSHAKE_DATA = AttributeKey.newInstance("MiniConnect_HandshakeData");
    public static final AttributeKey<Boolean> SKIP_BACKEND_PROXY = AttributeKey.valueOf(SKIP_BACKEND_PROXY_NAME);

}
