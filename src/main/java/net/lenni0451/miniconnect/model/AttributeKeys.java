package net.lenni0451.miniconnect.model;

import io.netty.util.AttributeKey;
import org.apache.commons.lang3.tuple.Pair;

public class AttributeKeys {

    public static final AttributeKey<ConnectionInfo> CONNECTION_INFO = AttributeKey.newInstance("MiniConnect_ConnectionInfo");
    public static final AttributeKey<Boolean> ENABLE_HAPROXY = AttributeKey.newInstance("MiniConnect_EnableHAProxy");
    public static final AttributeKey<Pair<String, Integer>> HANDSHAKE_DATA = AttributeKey.newInstance("MiniConnect_HandshakeData");

}
