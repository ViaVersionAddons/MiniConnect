package net.lenni0451.miniconnect.model;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.tuple.Triple;

public class AttributeKeys {

    public static final AttributeKey<ConnectionInfo> CONNECTION_INFO = AttributeKey.newInstance("MiniConnect_ConnectionInfo");
    public static final AttributeKey<Boolean> ENABLE_HAPROXY = AttributeKey.newInstance("MiniConnect_EnableHAProxy");
    public static final AttributeKey<Triple<String, Integer, ProtocolVersion>> HANDSHAKE_DATA = AttributeKey.newInstance("MiniConnect_HandshakeData");

}
